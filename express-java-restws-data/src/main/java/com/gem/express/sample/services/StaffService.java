package com.gem.express.sample.services;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gem.commons.TxResult;
import com.gem.express.sample.entities.Staff;

@Service
public class StaffService {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<Staff> list() {

		String jpql;

		jpql = "select s ";
		jpql += "from Staff s ";
		jpql += "order by s.lastName, s.firstName asc";

		Query q = em.createQuery(jpql);
		q.setMaxResults(1000);

		List<Staff> list = q.getResultList();
		
		detach(list);
		
		return list;
		
	}

	@Transactional
	public Staff create(Staff staff) {

		String firstName = staff.getFirstName();
		String lastName = staff.getLastName();
		byte active = staff.getActive();
		String email = staff.getEmail();
		String username = staff.getUsername();

		Staff ent = new Staff();
		ent.setFirstName(firstName);
		ent.setLastName(lastName);
		ent.setActive(active);
		ent.setEmail(email);
		ent.setUsername(username);
		ent.setLastUpdate(new Date());
		
		em.persist(ent);
		em.flush();
		
		detach(ent);
		return ent;
	}

	@Transactional
	public TxResult<Staff> put(long id, Staff staff) {

		Staff proxy = em.getReference(Staff.class, id);
		
		if (proxy == null) {
			Staff ent = create(staff);
			return new TxResult<>(true, ent);
		}

		String firstName = staff.getFirstName();
		String lastName = staff.getLastName();
		byte active = staff.getActive();
		String email = staff.getEmail();
		String username = staff.getUsername();

		proxy.setFirstName(firstName);
		proxy.setLastName(lastName);
		proxy.setActive(active);
		proxy.setEmail(email);
		proxy.setUsername(username);

		em.merge(proxy);
		em.flush();

		detach(proxy);
		return new TxResult<>(false, proxy);
		
	}
	
	@Transactional
	public boolean delete(long id) {

		String jpql;
		jpql = "delete s ";
		jpql += "from Staff s ";
		jpql += "where s.id = :id";

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
