package com.gem.auth.services;

import com.gem.auth.entities.Realm;
import com.gem.auth.entities.User;
import com.gem.commons.services.AbstractService;
import com.gem.commons.services.Params;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

import static com.gem.commons.Checker.checkParamIsPositive;
import static com.gem.commons.Checker.checkParamNotNull;

@Service
public class UserService extends AbstractService<User> {

    public UserService(){
        super(User.class);
    }


    public List<User> list(String realm){
        return where("e.realm.name = :realm", "realm", realm);
    }

    public User get(long realmId, long userId){
        checkParamIsPositive("realmId", realmId);
        checkParamIsPositive("userId", userId);

        Params p = new Params();
        p.set("realmId", realmId);
        p.set("userId", userId);

        String jpql;
        jpql = "e.realm.id = :realmId ";
        jpql += "and e.id = :id";

        return whereSingle(jpql, p);
    }

    @Transactional
    public void post(long realmId, User user) {
        checkParamNotNull("user", user);
        checkParamIsPositive("realmId", realmId);

        String name = user.getName();
        String pass = user.getPass();

        Verifier.checkUserName(name);

        user.setRealm(new Realm(realmId));

        user.setPass(DigestUtils.sha256Hex(pass));
        user.setPrevPass(null);
        user.setActivated(false);
        user.setReqPassChange(false);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy(null);
        user.setLastModifiedDate(null);

        super.post(user);
    }
}
