package com.gem.config.ws.services;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.Utils;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.PipeLine;
import com.gem.commons.mongo.Query;
import com.gem.commons.rest.ConflictException;
import com.gem.config.ws.entities.Property;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
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
	
	private void project(PipeLine p, boolean includeKeys) {

		Json c = new Json();

		c.put("_id", 			"$properties._id");
		c.put("name", 			"$properties.name");
		c.put("label", 			"$properties.label");
		c.put("lastUpdate",		"$properties.lastUpdate");
		c.put("creationDate",	"$properties.creationDate");
		//optional fields
		if(includeKeys){
			c.put("keys",	"$properties.keys");
		}

		p.project(c);
	}

	PipeLine pipe(String app){
		return pipe(app, false);
	}

	PipeLine pipe(String app, boolean includeKeys){
		checkParamNotNull("app", app);

		PipeLine p = new PipeLine();
		p.match("name", app);

		Json p1 = new Json();
		p1.put("properties", 1);
		p1.put("_id", 0); //app's id (we don't need it)

		p.project(p1);
		p.unwind("$properties");

		project(p, includeKeys);

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
		checkParamNotNull("app", app);
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

	public Property get(String app, ObjectId id) {
		checkParamNotNull("app", app);
		checkParamNotNull("id", id);

		app = Verifier.checkId("app", app);
		appSrv.checkExist(app);


		PipeLine p = pipe(app);
		p.match("_id", id);
		List<Property> list = apps.aggregateAndCollect(p, Property.class);
		if (list.isEmpty() || list.get(0) == null) {
			throw new NotFoundException("The property does not exist.");
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
		long count = apps.count(p);

		return count > 0;
	}

	public void checkExist(String app, String name) {

		if (exist(app, name) == false) {
			throw new BadRequestException("The property '" + name + "' does not exist.");
		}
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
		
		String label = Utils.strip(prop.getLabel());
		
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
		
		long ans = apps.updateOne(q);

		if (ans != 1) {
			throw new ConflictException("The app have been previously modified.");
		}

		return ent;
	}

	public TxResult<Property> put(String app, ObjectId id, Property prop) {
		checkParamNotNull("app", app);
		checkParamNotNull("id", id);
		checkParamNotNull("prop", prop);
		checkParamNotNull("prop.name", prop.getName());
		
		String name = Verifier.checkId("name", prop.getName());

		appSrv.checkExist(app);

		Property ref = get(app, id);
		if(ref.getName().equals(name) == false){
			// it's a rename, we have to check if the name is available
			checkIsAvailable(app, name);
		}

		String label = Utils.strip(prop.getLabel());

		Query q = new Query();
		q.filter("name", app); // app
		q.filter("properties._id", id);

		// The field properties is an array. But thanks to the filter, hopefully
		// it will bring just 1 property.
		q.update("properties.$.name", name);
		q.update("properties.$.label", label);
		q.update("properties.$.lastUpdate", new Date());

		long count = apps.updateOne(q);

		if (count == 1) {
			Property ans = new Property();
			ans.setId(id);
			ans.setName(name);

			return new TxResult<>(false, ans);
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

		Json criteria = new Json();
		criteria.put("name", name);

		q.pull("properties", criteria);
		
		long ans =  apps.updateOne(q);
		return ans > 0;
	}
}
