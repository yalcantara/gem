package com.gem.commons.services;


import com.gem.commons.Utils;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.gem.commons.Checker.*;

public abstract class AbstractService<E> {


    public static final int DEFAULT_MAX_RESULTS = 1000;

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[]{};
    private static final Object[] EMPTY_OBJECT_ARRAY = new Class[]{};


    @PersistenceContext
    protected EntityManager em;

    protected final boolean tenant;
    private final Class<?> tenantClass;
    private final Constructor<?> tenantConstructor;

    protected final Class<E> entityClass;
    protected final String entityName;

    protected AbstractService(Class<E> entityClass){
        checkParamNotNull("entityClass", entityClass);
        this.entityClass = entityClass;
        entityName = entityClass.getSimpleName();



        MultiTenant a = this.getClass().getDeclaredAnnotation(MultiTenant.class);
        if(a != null && Utils.containsField(entityClass, "tenant")){
            tenant = true;

            tenantClass = Utils.getFieldType(entityClass, "tenant");
            if(tenantClass == null){
                throw new RuntimeException("Could not obtain the type of the tenant field in " + entityName +".");
            }

            try {
                tenantConstructor = tenantClass.getConstructor(EMPTY_CLASS_ARRAY);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        }else if(a == null && Utils.containsField(entityClass, "tenant") == false){
            tenant = false;



            tenantClass = null;
            tenantConstructor = null;
        }else if(a == null){
            throw new IllegalArgumentException("Incongruent Tenant declaration. " +
                    "The @MultiTenant must be present on the service and the " +
                    "field 'tenant' in the entity. Missing: @Tenant on service.");
        }else{
            throw new IllegalArgumentException("Incongruent Tenant declaration. " +
                    "The @MultiTenant must be present on the service and the " +
                    "field 'tenant' in the entity. Missing: tenant field on entity.");
        }
    }

    private void assignTenant(E ent){

        if(tenant == false){
            return;
        }

        long tenantId = TenantHolder.getId();

        Object tenantObj;
        try {
            tenantObj = tenantConstructor.newInstance(EMPTY_OBJECT_ARRAY);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }


        try {
            BeanUtils.setProperty(tenantObj, "id", tenantId);
            BeanUtils.setProperty(ent, "tenant", tenantObj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected E getReference(long id){
        return em.getReference(entityClass, id);
    }

    private void multiTenantPredicate(CriteriaBuilder cb, Root<E> root, List<Predicate> pred){
        if(tenant) {
            long tenantId = TenantHolder.getId();
            Predicate tenantPred = cb.equal(root.get("tenant.id"), tenantId);

            pred.add(tenantPred);
        }
    }

    private <T> TypedQuery<T> queryFor(CriteriaBuilder cb, CriteriaQuery<T> cq, Root<E> root){

        //Multi-Tenant
        //=====================================================================
        if(tenant) {
            List<Predicate> pred = new ArrayList<>();
            multiTenantPredicate(cb, root, pred);
            Predicate[] arr = new Predicate[pred.size()];
            pred.toArray(arr);
            cq.where(arr);
        }
        //=====================================================================

        return em.createQuery(cq);
    }


    public long count(){
        return __count(null);
    }


    protected long count(String name, Object val){
        checkParamNotNull("name", name);
        return __count(whereParams(name, val));
    }

    private long count(Params params){
        checkParamNotNull("params", params);
        checkParamHigherThan("params.size", 0, params.size());
        return __count(params);
    }


    private long __count(Params params){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<E> root = cq.from(entityClass);
        cq.select(cb.count(root));

        TypedQuery<Long> q;
        if(params == null){
            q = queryFor(cb, cq, root);
        }else{
            q = whereQuery(cb, cq, params);
        }
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

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityClass);

        Root<E> root = cq.from(entityClass);
        cq.select(root);

        TypedQuery<E> query = queryFor(cb, cq, root);

        return resultList(query);
    }

    public E get(long id){
        checkParamIsPositive("id", id);


        E ent = whereSingle("id", id);
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

        //Multi-Tenant
        //=====================================================================
        assignTenant(ent);
        //=====================================================================

        em.persist(ent);
        em.flush();
        detach(ent);
    }


    protected boolean doDelete(long id){
        checkParamIsPositive("id", id);

        CriteriaBuilder cb  = em.getCriteriaBuilder();
        CriteriaDelete<E> query = cb.createCriteriaDelete(entityClass);
        Root<E> root = query.from(entityClass);

        Predicate idPred = cb.equal(root.get("id"), id);


        List<Predicate> pred = new ArrayList<>();
        pred.add(idPred);

        //Multi-Tenant
        //=====================================================================
        multiTenantPredicate(cb, root, pred);
        //=====================================================================

        Predicate[] arr = new Predicate[pred.size()];
        pred.toArray(arr);
        query.where(arr);

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


        //Multi-Tenant
        //=====================================================================
        multiTenantPredicate(cb, root, pred);
        //=====================================================================


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
