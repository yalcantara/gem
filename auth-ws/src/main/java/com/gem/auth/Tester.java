package com.gem.auth;

import com.gem.commons.Json;
import com.gem.commons.rest.RestClient;

import javax.ws.rs.core.*;

public class Tester {

    public static void main(String[] args) {
        RestClient c = new RestClient();

        var url = "http://localhost:8080/rest/realms/1/users";

        Json json = new Json();
        json.put("name", "bloop");
        json.put("realmId", 1);
        json.put("pass", "pass1");

        Response r = c.post(url, json);
        System.out.println(r);
        String ent = r.readEntity(String.class);
        System.out.println(ent);
    }
}
