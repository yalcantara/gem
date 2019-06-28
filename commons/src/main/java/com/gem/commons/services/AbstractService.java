package com.gem.commons.services;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.*;
import java.util.*;

import static com.gem.commons.Checker.*;

public abstract class AbstractService<E> {


    public static final int DEFAULT_MAX_RESULTS = 1000;


    @PersistenceContext
    protected EntityManager em;

    protected final Class<E> entityClass;
    protected final String entityName;

    protected AbstractService(Class<E> entityClass){
        checkParamNotNull("entityClass", entityClass);
        this.entityClass = entityClass;
        entityName = entityClass.getSimpleName();
    }

    private String jpql_select(){
        String jpql;

        //Intened empty last character, for appending more statements
        jpql = "select e ";
        jpql += "from " + entityName + " e ";

        return jpql;
    }

    protected E getReference(long id){
        return em.getReference(entityClass, id);
    }

    public long count(){

        String jpql;
        jpql = "select count(e) ";
        jpql += "from " + entityName +" e";

        Query q = em.createQuery(jpql);
        q.setMaxResults(1);

        long count = (Long)q.getSingleResult();

        return count;
    }


    protected long count(String name, Object val){
        checkParamNotNull("name", name);
        return count(whereParams(name, val));
    }


    protected long count(Params params){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<E> root = cq.from(entityClass);
        cq.select(cb.count(root));

        TypedQuery<Long> q = whereQuery(cb, cq, params);
        q.setMaxResults(1);

        return q.getSingleResult();

    }


    protected boolean exist(String name, Object value){
        //it shouldn't be higher than 1 but, it let's do it this way
        //in there is some data incongruencies.
        return count(name, value) >= 1;
    }

    protected boolean exist(Params params){
        return count(params) >= 1;
    }

    public boolean exist(long id){
        checkParamIsPositive("id", id);
        return exist("id", id);
    }

    public List<E> list(){

        String jpql = jpql_select();
        jpql += "order by e.id asc";

        Query q = em.createQuery(jpql);
        return resultList(q);
    }

    public E get(long id){
        checkParamIsPositive("id", id);

        E ent = em.find(entityClass, id);
        if(ent == null){
            throw new NotFoundException("Could not find the entity with id " + id +".");
        }
        detach(ent);
        return ent;
    }

    protected E getBy(String name, Object value){
        E ent = whereSingle("name", value);
        if(ent == null){
            throw new NotFoundException("Could not find the entity with field " + name +" and value " + value +".");
        }
        detach(ent);
        return ent;
    }

    protected void doPost(E ent){
        checkParamNotNull("ent", ent);

        em.persist(ent);
        em.flush();
        detach(ent);
    }

    protected boolean doDelete(long id){
        checkParamIsPositive("id", id);

        CriteriaBuilder cb  = em.getCriteriaBuilder();
        CriteriaDelete<E> query = cb.createCriteriaDelete(entityClass);
        Root<E> root = query.from(entityClass);
        query.where(cb.equal(root.get("id"), id));

        Query q = em.createQuery(query);
        q.setMaxResults(1);
        int ans = q.executeUpdate();

        return ans > 0;
    }

    private Params whereParams(String name, Object value){
        Params p = new Params();
        p.set(name, value);
        return p;
    }

    public List<E> where(Params params){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);

        Root<E> root = cq.from(entityClass);
        cq.select(root);

        return resultList(whereQuery(cb, cq, params));
    }

    public List<E> where(String name, Object value){
        return where(name, value, null);
    }

    public List<E> where(String name, Object value, Integer maxResult){
        checkParamNotNull("name", name);

        Params params = new Params();
        params.set("name", value);

        return where(params, maxResult);
    }

    protected E whereSingle(String name, Object value){
        checkParamNotNull("name", name);
        Params params = new Params();
        params.set(name, value);
        return whereSingle(params);
    }

    protected E whereSingle(Params params){
        List<E> list = where(params, 1);
        return (list.size() > 0)?list.get(0):null;
    }

    protected List<E> where(Params params, Integer maxResults){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);

        Root<E> root = cq.from(entityClass);
        cq.select(root);

        return resultList(whereQuery(cb, cq, params), maxResults);
    }

    private <T> TypedQuery<T> whereQuery(CriteriaBuilder cb, CommonAbstractCriteria caq, Params params){
        checkParamNotNull("params", params);
        checkParamHigherThan("params.size", 0, params.size());

        Set<Map.Entry<String, Object>> entries = params.entrySet();


        Root<E> root;
        if(caq instanceof CriteriaQuery){
            CriteriaQuery cq = ((CriteriaQuery)caq);

            root = cq.from(entityClass);
        }else{
            throw new UnsupportedOperationException("Criteria " + caq.getClass().getSimpleName() + " not suported.");
        }

        List<Predicate> pred = new ArrayList<>();
        for(Map.Entry<String, Object> e:entries){

            String name = e.getKey();
            Object val = e.getValue();

            Predicate and = cb.equal(root.get(name), val);
            pred.add(and);
        }

        Predicate[] arr = new Predicate[pred.size()];
        pred.toArray(arr);

        if(caq instanceof CriteriaQuery){
            CriteriaQuery cq = ((CriteriaQuery)caq);
            cq.where(cb.and(arr));
        }else{
            throw new UnsupportedOperationException("Criteria " + caq.getClass().getSimpleName() + " not suported.");
        }

        TypedQuery<T> q;

        if(caq instanceof CriteriaQuery) {
            CriteriaQuery cq = ((CriteriaQuery)caq);
            q = em.createQuery(cq);
        }else{
            throw new UnsupportedOperationException("Criteria " + caq.getClass().getSimpleName() + " not suported.");
        }

        return q;
    }

    private void setParams(Query q, Params params){
        for (Map.Entry<String, Object> entry:params.entrySet()){
            String key = entry.getKey();
            Object val = entry.getValue();

            q.setParameter(key, val);
        }
    }

    protected E resultSingle(Query q){
        q.setMaxResults(1);
        E ent = (E)q.getSingleResult();
        detach(ent);
        return ent;
    }

    protected List<E> resultList(Query q){
        return resultList(q, null);
    }

    protected List<E> resultList(Query q, Integer maxResults){
        if(maxResults == null){
            q.setMaxResults(DEFAULT_MAX_RESULTS);
        }else{
            q.setMaxResults(maxResults);
        }
        List<E> list = q.getResultList();
        detach(list);
        return list;
    }

    protected final void detach(Object obj){
        if(obj == null){
            return;
        }

        if(obj instanceof Collection){
            Collection col = (Collection) obj;
            for(Object e:col){
                em.detach(e);
            }
        }else{
            em.detach(obj);
        }
    }
}
