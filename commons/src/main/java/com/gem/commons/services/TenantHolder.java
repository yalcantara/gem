package com.gem.commons.services;

import static com.gem.commons.Checker.checkParamIsPositive;

public class TenantHolder {


    static final ThreadLocal<Long> tenantId = ThreadLocal.withInitial(()->null);

    public static void setId(Long id){
        if(id != null){
            checkParamIsPositive("id", id);
        }
        tenantId.set(id);
    }


    public static long getId(){
        return getId(true);
    }

    public static Long getId(boolean check){
        Long id = tenantId.get();
        if(check && id == null){
            throw new IllegalArgumentException("The tenant id is required as a side parameter.");
        }

        return id;
    }
}
