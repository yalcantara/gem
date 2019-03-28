package com.gem.config.ws.services;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.Query;
import com.gem.commons.rest.ConflictException;
import com.gem.config.ws.entities.App;

@Service
public class AppService {
	
	@Inject
	@Qualifier("apps")
	private Collection apps;

	@SuppressWarnings("unchecked")
	public List<App> list() {

		return apps.find();
	}
	
	public App get(String name) {
		checkParamNotNull("name", name);
		App dto = (App) apps.findOne("name", name);

		if (dto == null) {
			throw new NotFoundException("The app '" + name + "' does not exist.");
		}

		return dto;
	}
	
	public App get(ObjectId id) {
		checkParamNotNull("id", id);
		App dto = (App) apps.findOne(id);
		if (dto == null) {
			throw new NotFoundException("The app does not exist.");
		}

		return dto;
	}
	
	public boolean exist(String name) {
		checkParamNotNull("name", name);
		
		name = Verifier.checkId("name", name);
		
		Json q = new Json();
		q.put("name", name);
		
		long count = apps.count(q);
		
		return count > 0;
	}

	public boolean isAvailable(String name) {
		return exist(name) == false;
	}

	public void checkIsAvailable(String name) {
		
		if (exist(name)) {
			throw new ConflictException("The app '" + name + "' already exists.");
		}
	}

	public void checkExist(String name) {
		
		if (exist(name) == false) {
			throw new BadRequestException("The app '" + name + "' does not exist.");
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

		name = Verifier.checkId("name", name);
		
		checkIsAvailable(name);

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
	
	public TxResult<App> put(ObjectId id, App app) {
		checkParamNotNull("id", id);
		checkParamNotNull("app", app);
		checkParamNotNull("app.name", app.getName());
		
		String name = app.getName();
		name = Verifier.checkId("name", name);
		
		App ref = get(id);
		
		if (ref.getName().equals(name) == false) {
			// it's a rename, we have to check if the name is available
			checkIsAvailable(name);
		}
		
		app.setName(name);
		
		String label = app.getLabel();
		if (label != null) {
			label = label.strip();
			app.setLabel(label);
		}
		
		Query q = new Query();
		q._id(id);
		
		q.update("name", app.getName());
		q.update("label", app.getLabel());
		q.update("lastUpdate", new Date());
		
		long count = apps.update(q);
		
		if (count >= 1) {
			App ans = new App();
			ans.setId(id);
			ans.setName(name);
			
			return new TxResult<App>(false, ans);
		}
		
		throw new ConflictException("The app about to be updated could not be found.");
	}

	public boolean delete(String name) {
		checkParamNotNull("name", name);
		
		name = Verifier.checkId("name", name);
		return apps.deleteMany("name", name);
	}
}
