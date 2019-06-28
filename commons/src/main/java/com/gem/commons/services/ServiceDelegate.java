package com.gem.commons.services;

public class ServiceDelegate<E> extends AbstractService<E> {


    public ServiceDelegate(Class<E> entityClass){
        super(entityClass);
    }
}
