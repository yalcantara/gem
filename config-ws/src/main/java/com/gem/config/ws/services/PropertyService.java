package com.gem.config.ws.services;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.mongo.Collection;
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

	public List<Property> list(String app) {
		checkParamNotNull("app", app);

		Query q = new Query();
		q.filter("name", app);
		q.include("properties");
		
		List<Property> list = apps.findOneAndConvertList(q, "properties", Property.class);
		return list;
	}
	
	public Property get(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);
		
		Query q = new Query();
		q.filter("name", app); // app
		q.filter("properties.name", name);

		q.include("properties");

		List<Property> list = apps.findOneAndConvertList(q, "properties", Property.class);
		if (list == null) {
			return null;
		}
		
		return list.get(0);
	}
	
	public boolean exist(String app, String name) {
		checkParamNotNull("app", name);
		checkParamNotNull("name", name);
		
		name = Verifier.pack(name);
		
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
		appSrv.checkName(app);
		
		return exist(app, name) == false;
	}

	public void checkName(String app, String name) {
		
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
		Verifier.checkId("name", name);

		name = Verifier.pack(name);
		
		checkName(app, name);

		String label = prop.getLabel();
		if (label != null) {
			label = label.strip();
		}

		Property ent = new Property();
		ent.setName(name);
		ent.setLabel(label);
		
		Date now = new Date();
		ent.setCreationDate(now);
		ent.setLastUpdate(now);
		
		apps.insert(ent);
		return ent;
	}
	
	public TxResult<Property> put(String app, String name, Property prop) {
		checkParamNotNull("app", app);
		checkParamNotNull("name", name);
		checkParamNotNull("prop", prop);
		checkParamNotNull("prop.name", prop.getName());

		name = Verifier.pack(name);
		if (isAvailable(app, name)) {
			prop.setName(name);
			Property ent = create(app, prop);
			return new TxResult<>(true, ent);
		}

		String newName = prop.getName();
		Verifier.checkId("name", newName);
		newName = Verifier.pack(newName);

		checkName(app, newName);
		
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
		
		app = Verifier.pack(app);
		name = Verifier.pack(name);
		
		Query q = new Query();
		q.filter("name", app); // app
		q.filter("properties.name", name); // property

		return apps.deleteOne(q);
	}
}
