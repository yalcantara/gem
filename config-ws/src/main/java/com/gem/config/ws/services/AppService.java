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
import com.gem.commons.rest.exceptions.ConflictException;
import com.gem.config.ws.entities.App;

@Service
public class AppService {
	
	@Inject
	@Qualifier("apps")
	private Collection apps;

	@SuppressWarnings("unchecked")
	public List<App> list() {

		return apps.find(1000);
	}
	
	public App get(String name) {

		return (App) apps.findOne("name", name);
	}
	
	public boolean exist(String name) {
		checkParamNotNull("name", name);
		
		name = Verifier.pack(name);
		
		Json q = new Json();
		q.put("name", name);
		
		long count = apps.count(q);
		
		return count > 0;
	}

	public boolean isAvailable(String name) {
		return exist(name) == false;
	}

	public void checkName(String name) {
		
		if (exist(name)) {
			throw new ConflictException("The app '" + name + "' already exists.");
		}
	}
	
	public App create(String name) {
		App app = new App();
		app.setName(name);
		return create(app);
	}
	
	public App create(App app) {
		checkParamNotNull("app", app);
		checkParamNotNull("app.name", app.getName());
		
		String name = app.getName();
		Verifier.checkId("name", name);

		name = Verifier.pack(name);
		
		checkName(name);

		String label = app.getLabel();
		if (label != null) {
			label = label.strip();
		}

		App ent = new App();
		ent.setName(name);
		ent.setLabel(label);
		
		Date now = new Date();
		ent.setCreationDate(now);
		ent.setLastUpdate(now);
		
		apps.insert(ent);
		return ent;
	}
	
	public TxResult<App> put(String name, App app) {
		checkParamNotNull("name", name);
		checkParamNotNull("app", app);
		checkParamNotNull("app.name", app.getName());

		name = Verifier.pack(name);
		if (isAvailable(name)) {
			app.setName(name);
			App ent = create(app);
			return new TxResult<>(true, ent);
		}

		String newName = app.getName();
		Verifier.checkId("name", newName);
		checkName(newName);
		
		Query q = new Query();
		q.update("name", newName);
		q.update("label", app.getLabel());
		q.update("lastUpdate", new Date());
		
		App old = (App) apps.update("name", name, q);
		
		if (old == null) {
			throw new ConflictException("The app was deleted while processing it.");
		}
		
		// let us return the 2 most important fields for later use of
		// finding this resource.
		App ans = new App();
		ans.setId(old.getId());
		ans.setName(newName);
		
		return new TxResult<App>(false, ans);
	}

	public boolean delete(String name) {
		checkParamNotNull("name", name);
		
		name = Verifier.pack(name);
		return apps.deleteOne("name", name);
	}
}
