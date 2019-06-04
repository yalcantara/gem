package com.gem.auth.services;

import com.gem.auth.entities.Realm;
import com.gem.commons.services.AbstractService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.*;

import static com.gem.commons.Checker.checkParamIsPositive;

@Service
public class RealmService extends AbstractService<Realm> {


    public RealmService(){
        super(Realm.class);
    }


    public void checkExist(long id){
        checkParamIsPositive("id", id);
        if(exist(id)){
            return;
        }

        throw new NotFoundException("The realm with id " + id +" does not exist.");
    }

    @Transactional
    public void post(Realm realm){
        super.post(realm);
    }


}
