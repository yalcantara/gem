package com.gem.config.ws.services;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gem.commons.TxResult;
import com.gem.commons.rest.exceptions.ConflictException;
import com.gem.config.ws.entities.App;

@Service
public class AppService {
	
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<App> list() {

		String jpql;

		jpql = "select a ";
		jpql += "from App a ";
		jpql += "order by a.name asc";

		Query q = em.createQuery(jpql);
		q.setMaxResults(1000);

		List<App> list = q.getResultList();
		
		detach(list);
		
		return list;
	}
	
	public void checkName(String name) {
		checkParamNotNull("name", name);

		name = Verifier.pack(name);

		String jpql;
		jpql = "select count(a) ";
		jpql += "from App a ";
		jpql += "where a.name = :name";

		Query q = em.createQuery(jpql);
		q.setMaxResults(1);
		q.setParameter("name", name);

		long count = ((Number) q.getSingleResult()).longValue();

		if (count >= 1) {
			throw new ConflictException("The app '" + name + "' already exists.");
		}

	}
	
	@Transactional
	public App create(String name) {
		App app = new App();
		app.setName(name);
		return create(app);
	}
	
	@Transactional
	public App create(App app) {
		checkParamNotNull("app", app);
		checkParamNotNull("app.name", app.getName());

		String name = app.getName();
		name = name.toLowerCase();
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

		em.persist(ent);
		em.flush();
		
		detach(ent);
		return ent;
	}

	@Transactional
	public TxResult<App> put(long id, App app) {
		checkParamIsPositive("id", id);
		checkParamNotNull("app", app);
		checkParamNotNull("app.name", app.getName());
		
		App proxy = em.getReference(App.class, id);
		
		if (proxy == null) {
			App ent = create(app);
			return new TxResult<>(true, ent);
		}

		String name = app.getName();

		checkName(name);
		proxy.setName(name);
		proxy.setLastUpdate(new Date());

		em.merge(proxy);
		em.flush();

		detach(proxy);
		return new TxResult<App>(false, proxy);
	}
	
	@Transactional
	public boolean delete(long id) {
		checkParamIsPositive("id", id);

		String jpql;
		jpql = "delete a ";
		jpql += "from App a ";
		jpql += "where a.id = :id";

		Query q = em.createQuery(jpql);
		q.setParameter("id", id);

		int updated = q.executeUpdate();

		if (updated > 0) {
			return true;
		}

		return false;
	}
	
	@SuppressWarnings("rawtypes")
	private void detach(Object obj) {
		if (obj == null) {
			return;
		}
		
		if (obj instanceof Collection) {
			detach((Collection) obj);
			return;
		}
		
		em.detach(obj);
	}

	@SuppressWarnings("rawtypes")
	private void detach(Collection list) {
		if (list == null) {
			return;
		}

		for (Object e : list) {
			if (e != null) {
				em.detach(e);
			}
		}
	}
}
