package com.gem.auth.services;

import com.gem.auth.entities.Realm;
import com.gem.commons.services.AbstractService;
import com.gem.commons.services.MultiTenant;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@MultiTenant
public class RealmService extends AbstractService<Realm>  {

    public RealmService(){
        super(Realm.class);
    }





    @Transactional
    public void post(Realm realm){
        super.doPost(realm);
    }







}
