package com.gem.config.ws.services;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.PipeLine;
import com.gem.commons.mongo.Query;
import com.gem.commons.rest.ConflictException;
import com.gem.config.ws.entities.Property;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

import static com.gem.commons.Checker.checkParamNotNull;

@Service
public class PropertyService {

	@Inject
	@Qualifier("apps")
	private Collection apps;
	
	@Inject
	private AppService appSrv;
	
	private void project(PipeLine p) {

		Json c = new Json();

		c.put("_id", 			"$properties._id");
		c.put("name", 			"$properties.name");
		c.put("label", 			"$properties.label");
		c.put("lastUpdate",		"$properties.lastUpdate");
		c.put("creationDate",	"$properties.creationDate");

		p.project(c);
	}

	private PipeLine pipe(String app){

		PipeLine p = new PipeLine();
		p.match("name", app);

		Json p1 = new Json();
		p1.put("properties", 1);
		p1.put("_id", 0); //app's id (we don't need it)

		p.project(p1);
		p.unwind("$properties");
		project(p);

		return p;

	}

	public List<Property> list(String app) {
		checkParamNotNull("app", app);
		
		app = Verifier.checkId("app", app);
		appSrv.checkExist(app);
		
		PipeLine p = pipe(app);

		List<Property> list = apps.aggregateAndCollect(p, Property.class);
		return list;
	}

	public Property get(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);

		app = Verifier.checkId("app", app);
		name = Verifier.checkId("name", name);
		appSrv.checkExist(app);


		PipeLine p = pipe(app);
		p.match("name", name);
		List<Property> list = apps.aggregateAndCollect(p, Property.class);
		if (list.isEmpty() || list.get(0) == null) {
			throw new NotFoundException("The property '" + name + "' does not exist.");
		}

		Property dto = list.get(0);
		
		return dto;
	}

	public boolean exist(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);

		app = Verifier.checkId("app", app);
		name = Verifier.checkId("name", name);

		PipeLine p = pipe(app);
		p.match("name", name);
		p.count();

		AggregateIterable<Document> a = apps.aggregate(p, Document.class);

		try (MongoCursor<Document> iter = a.iterator()) {
			if (iter.hasNext()) {
				Document ans = iter.next();
				
				long val = ((Number) ans.get("count")).longValue();
				
				return val >= 1;
			}

			throw new InternalServerErrorException("Could not count documents.");
		}
	}
	
	public boolean isAvailable(String app, String name) {
		return exist(app, name) == false;
	}
	
	public void checkIsAvailable(String app, String name) {

		if (exist(app, name)) {
			throw new ConflictException("The property '" + name + "' already exists.");
		}
	}

	public Property create(String app, Property prop) {
		checkParamNotNull("app", app);
		checkParamNotNull("prop", prop);
		checkParamNotNull("prop.name", prop.getName());

		String name = prop.getName();

		app = Verifier.checkId("app", app);
		name = Verifier.checkId("name", name);

		appSrv.checkExist(app);
		checkIsAvailable(app, name);
		
		String label = prop.getLabel();
		if (label != null) {
			label = label.strip();
		}
		
		Property ent = new Property();
		ent.setId(new ObjectId());
		ent.setName(name);
		ent.setLabel(label);
		
		Date now = new Date();
		ent.setCreationDate(now);
		ent.setLastUpdate(now);
		
		Query q = new Query();
		q.filter("name", app);
		q.push("properties", ent);
		
		long ans = apps.update(q);

		if (ans != 1) {
			throw new ConflictException("The app have been previously modified.");
		}

		return ent;
	}

	public TxResult<Property> put(String app, String name, Property prop) {
		checkParamNotNull("app", app);
		checkParamNotNull("name", name);
		checkParamNotNull("prop", prop);
		checkParamNotNull("prop.name", prop.getName());
		
		name = Verifier.checkId("name", name);
		if (isAvailable(app, name)) {
			prop.setName(name);
			Property ent = create(app, prop);
			return new TxResult<>(true, ent);
		}
		
		String newName = prop.getName();
		newName = Verifier.checkId("newName", newName);

		appSrv.checkExist(app);
		if(newName.equals(name) == false) {
			//It's a name change. We have to make sure that the new name is available.
			checkIsAvailable(app, newName);
		}

		Query q = new Query();
		q.filter("name", app); // app
		q.filter("properties.name", name);

		// The field properties is an array. But thanks to the filter, hopefully
		// it will bring just 1 property.
		q.update("properties.name", newName);
		q.update("properties.label", prop.getLabel());
		q.update("properties.lastUpdate", new Date());

		long count = apps.update(q);

		if (count == 1) {
			Property ans = new Property();
			ans.setName(newName);

			return new TxResult<Property>(false, ans);
		}

		throw new ConflictException("The app or the property have been previously modified.");
	}
	
	public boolean delete(String app, String name) {
		checkParamNotNull("app", app);
		checkParamNotNull("name", name);

		app = Verifier.checkId("app", app);
		name = Verifier.checkId("name", name);

		Query q = new Query();
		q.filter("name", app); // app
		q.filter("properties.name", name); // property
		
		return apps.deleteOne(q);
	}
}
