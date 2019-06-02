package com.gem.commons.services;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

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

        jpql = "select e ";
        jpql += "from " + entityName + " e";

        return jpql;
    }

    public List<E> list(){

        String jpql = jpql_select();

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


    protected E merge(E ent){
        checkParamNotNull("ent", ent);

        E ans = em.merge(ent);
        em.flush();
        detach(ans);

        return ans;
    }

    protected void post(E ent){
        checkParamNotNull("ent", ent);

        em.persist(ent);
        em.flush();
        detach(ent);
    }

    protected boolean delete(long id){
        checkParamIsPositive("id", id);

        String jpql;
        jpql = "delete ";
        jpql += "from " + entityName +" e ";
        jpql += "where e.id = :id";

        Query q = em.createQuery(jpql);
        q.setParameter("id", id);
        int ans = q.executeUpdate();

        return ans > 0;
    }

    private Params whereParams(String name, Object value){
        Params p = new Params();
        p.set(name, value);
        return p;
    }

    protected List<E> where(String condition, String name, Object value){
        checkParamNotNull("condition", condition);

        return resultList(whereQuery(condition, whereParams(name, value)));
    }

    protected E whereSingle(String condition, Params params){
        checkParamNotNull("condition", condition);

        return resultSingle(whereQuery(condition, params));
    }

    private Query whereQuery(String condition, Params params){
        checkParamNotNull("condition", condition);
        checkParamNotNull("params", params);

        String jpql = jpql_select();

        jpql += " where " + condition;

        Query q = em.createQuery(jpql);
        for (Map.Entry<String, Object> entry:params.entrySet()){
            String key = entry.getKey();
            Object val = entry.getValue();

            q.setParameter(key, val);
        }

        return q;
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
            for(Object e:(Collection)obj){
                em.detach(e);
            }
        }else{
            em.detach(obj);
        }
    }
}
