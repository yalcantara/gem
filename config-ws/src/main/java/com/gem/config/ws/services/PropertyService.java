package com.gem.config.ws.services;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.PipeLine;
import com.gem.commons.mongo.Query;
import com.gem.commons.rest.ConflictException;
import com.gem.config.ws.entities.Property;

@Service
public class PropertyService {

	@Inject
	@Qualifier("apps")
	private Collection apps;
	
	@Inject
	private AppService appSrv;
	
	private void include(Query q) {
		q.include("properties._id");
		q.include("properties.name");
		q.include("properties.label");
		q.include("properties.lastUpdate");
		q.include("properties.creationDate");
	}

	public List<Property> list(String app) {
		checkParamNotNull("app", app);
		
		app = Verifier.checkId("app", app);
		appSrv.checkExist(app);
		
		Query q = new Query();
		q.filter("name", app); // app
		include(q);

		List<Property> list = apps.findOneAndConvertList(q, "properties", Property.class);
		return list;
	}

	public Property get(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);
		
		PipeLine pipe = new PipeLine();
		pipe.match("name", app);
		pipe.projects("_id", "properties");
		pipe.unwind("properties");
		pipe.match("properties.name", name);
		
		List<Property> list = apps.aggregateAndConvertObject(pipe, "properties", Property.class);
		if (list.isEmpty()) {
			throw new NotFoundException("The property '" + name + "' does not exist.");
		}

		Property dto = list.get(0);
		
		if (dto == null) {
			throw new NotFoundException("The property '" + name + "' does not exist.");
		}
		
		return dto;
	}

	public boolean exist(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);

		name = Verifier.checkId("name", name);

		Json q = new Json();
		q.put("name", name); // app's name
		q.put("properties.name", name); // prop's name

		long count = apps.count(q);

		return count > 0;
	}
	
	public boolean isAvailable(String app, String name) {

		// here we need to call for check the app's name because it should have
		// been created before asking for configuration's new name. If not, it
		// is ok to launch the ConflictException by the AppService.
		appSrv.checkIsAvailable(app);

		return exist(app, name) == false;
	}
	
	public void checkIsAvailable(String app, String name) {

		if (exist(app, name)) {
			throw new ConflictException("The property '" + name + "' already exists.");
		}
	}

	public Property create(String app, String name) {
		Property prop = new Property();
		prop.setName(name);
		return create(app, prop);
	}

	public Property create(String app, Property prop) {
		checkParamNotNull("app", app);
		checkParamNotNull("prop", prop);
		checkParamNotNull("prop.name", prop.getName());

		String name = prop.getName();
		
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
		
		checkIsAvailable(app, newName);

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
