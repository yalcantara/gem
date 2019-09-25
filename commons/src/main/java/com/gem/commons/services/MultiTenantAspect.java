package com.gem.commons.services;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class MultiTenantAspect {



    private static final ThreadLocal<Long> tenantLocal = ThreadLocal.withInitial(()->null);


    @Around("@target(com.gem.commons.services.MultiTenant)")
    public void multiTenantService(ProceedingJoinPoint pjp)throws Throwable{

        boolean root = false;
        try{
            Long id = tenantLocal.get();
            if(id == null){
                id = TenantHolder.getId();
                tenantLocal.set(id);
                root = true;
            }

            if(id == null){
                throw new RuntimeException("The tenant id is not present in the call stack.");
            }
            pjp.proceed();
        }finally{
            if(root){
                tenantLocal.set(null);
            }
            TenantHolder.setId(null);
        }
    }


}
