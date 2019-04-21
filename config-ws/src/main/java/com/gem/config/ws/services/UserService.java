package com.gem.config.ws.services;


import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.Query;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


@Service
public class UserService {

    @Inject
    @Qualifier("users")
    private Collection users;



    public boolean match(String user, String pass){

        String sha256 = DigestUtils.sha256Hex(pass);

        Query q = new Query();
        q.filter("name", user);
        q.filter("pass", sha256);
        long count = users.count(q);

        return count > 0;
    }
}
