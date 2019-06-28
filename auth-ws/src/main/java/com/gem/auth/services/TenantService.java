package com.gem.auth.services;

import com.gem.auth.entities.Tenant;
import com.gem.commons.rest.ConflictException;
import com.gem.commons.services.AbstractService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.Date;

import static com.gem.commons.Checker.*;
import static com.gem.commons.Utils.strip;

@Service
public class TenantService extends AbstractService<Tenant> {

    public TenantService(){
        super(Tenant.class);
    }



    private void validate(Tenant tenant){
        checkParamNotNull("tenant", tenant);

        //Tenant.name
        //---------------------------------------------------------------------
        String name = strip(tenant.getName());

        if(name == null){
            throw new BadRequestException("The name is required.");
        }
        name = name.toLowerCase();
        tenant.setName(name);
        //---------------------------------------------------------------------


        //Tenant.label (not required)
        //---------------------------------------------------------------------
        String label = strip(tenant.getLabel());
        tenant.setLabel(label);
        //---------------------------------------------------------------------

    }

    public Tenant getByName(String name){
        return getBy("name", name);
    }

    public boolean exist(String name){
        checkParamNotNull("name", name);
        return exist("name", name);
    }

    public void check(String name){
        if(exist(name)){
            throw new ConflictException("The name '" + name +"' is already in use.");
        }
    }

    @Transactional
    public void post(Tenant tenant){

        validate(tenant);

        String name = tenant.getName();
        check(name);

        tenant.setCreatedDate(new Date());
        doPost(tenant);
    }

    @Transactional
    public boolean delete(long id){
        return super.doDelete(id);
    }

    @Transactional
    public void put(Tenant tenant){
        checkParamNotNull("tenant", tenant);
        checkParamNotNull("tenant.id", tenant.getId());
        checkParamIsPositive("tenant.id", tenant.getId());


        long id = tenant.getId();
        Tenant proxy = getReference(id);

        if(proxy == null){
            throw new ConflictException("Could not find a tenant with id " +
                    id +". Maybe it was already deleted.");
        }

        validate(tenant);

        String name = tenant.getName();
        String label = tenant.getLabel();

        if(name.equals(proxy.getName()) == false){
            // it's a rename, we have to check if the name is available
            check(name);
        }


        proxy.setName(name);
        proxy.setLabel(label);
        proxy.setLastModifiedDate(new Date());

        em.merge(proxy);
        em.flush();
        detach(proxy);
    }
}
