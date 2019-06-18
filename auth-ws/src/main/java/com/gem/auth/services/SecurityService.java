package com.gem.auth.services;

import com.gem.auth.entities.InternalUser;
import com.gem.commons.services.AbstractService;
import com.gem.commons.services.Params;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;

import static com.gem.commons.Checker.checkParamNotNull;
import static com.gem.commons.Utils.strip;

@Service
public class SecurityService extends AbstractService<InternalUser> {

    public SecurityService() {
        super(InternalUser.class);
    }

    public InternalUser findByName(String name){
        checkParamNotNull("name", name);
        return whereSingle("e.name = :name", "name", name);
    }

    public boolean match(String user, String pass){

        user = strip(user);
        pass = strip(pass);

        if(user == null){
            throw new BadRequestException("The user is required.");
        }

        if(pass == null){
            throw new BadRequestException("The password is required.");
        }

        final String sha256 = DigestUtils.sha256Hex(pass);

        Params p = new Params();
        p.set("user", user);
        p.set("pass", sha256);

        boolean ans = exist("e.name = :user and e.pass = :pass", p);

        return ans;
    }
}
