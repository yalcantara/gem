package com.gem.auth.services;

import com.gem.auth.entities.Realm;
import com.gem.commons.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class RealmService extends AbstractService<Realm> {


    public RealmService(){
        super(Realm.class);
    }


}
