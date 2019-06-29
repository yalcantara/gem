package com.gem.auth.services;

import com.gem.auth.entities.Realm;
import com.gem.auth.entities.User;
import com.gem.commons.services.AbstractService;
import com.gem.commons.services.MultiTenant;
import com.gem.commons.services.Params;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.time.Instant;
import java.util.List;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;
import static com.gem.commons.Utils.strip;

@Service
@MultiTenant
public class UserService extends AbstractService<User> {

    @Inject
    private RealmService realmSrv;

    public UserService(){
        super(User.class);
    }


    public List<User> list(String realm){
        Params params = new Params();
        params.set("realm.name", realm);
        return where(params);
    }

    public User get(long realmId, long userId){
        checkParamIsPositive("realmId", realmId);
        checkParamIsPositive("userId", userId);

        Params p = new Params();
        p.set("realm.id", realmId);
        p.set("userId", userId);

        return whereSingle(p);
    }

    private void validate(long realmId, User user){
        checkParamNotNull("user", user);

        //User.name
        //---------------------------------------------------------------------
        String name = user.getName();
        name = strip(name);

        if(name == null){
            throw new BadRequestException("The user name is required.");
        }

        Verifier.checkUserName(name);
        name = name.toLowerCase();
        user.setName(name);
        //---------------------------------------------------------------------


        //User.pass
        //---------------------------------------------------------------------
        String pass = user.getPass();
        pass = strip(pass);

        if(pass == null){
            throw new BadRequestException("The password is required.");
        }
        Verifier.checkPassword(pass);
        user.setPass(pass);
        //---------------------------------------------------------------------

        //User.realm
        //---------------------------------------------------------------------
        //realmSrv.checkExist(realmId);
        //---------------------------------------------------------------------

        //User.createdBy
        //---------------------------------------------------------------------
        String createdBy = user.getCreatedBy();
        createdBy = strip(createdBy);
        if(createdBy == null){
            throw new BadRequestException("The created by field is required.");
        }
        user.setCreatedBy(createdBy);
        //---------------------------------------------------------------------
    }

    @Transactional
    public void post(long realmId, User user) {
        checkParamNotNull("user", user);
        checkParamIsPositive("realmId", realmId);


        validate(realmId, user);

        user.setRealm(new Realm(realmId));

        var pass = user.getPass();

        user.setPass(DigestUtils.sha256Hex(pass));
        user.setPrevPass(null);
        user.setActivated(false);
        user.setReqPassChange(false);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy(null);
        user.setLastModifiedDate(null);

        super.doPost(user);
    }
}
