package com.gem.config.ws.services;

import com.gem.commons.Json;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.PipeLine;
import com.gem.config.ws.entities.Key;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.gem.commons.Checker.checkParamNotNull;

@Service
public class KeyService {

    @Inject
    @Qualifier("apps")
    private Collection apps;


    public List<Key> list(String app, String prop){
        checkParamNotNull("app", app);
        checkParamNotNull("prop", prop);

        app = Verifier.checkId("app", app);
        prop = Verifier.checkId("prop", prop);


        /*
        {$match: {name: '911'}},
        {$project: {'properties': 1, _id: 1}},
        {$unwind: '$properties'},
        {$match: {'properties.name': 'email'}},
        {$project: {'properties.keys': 1}},
        {$unwind: '$properties.keys'},
        {$project: {name: '$properties.keys.name', value: '$properties.keys.value', _id: 0}}
        */

        PipeLine p = new PipeLine();
        p.match("name", app);
        p.project("properties");
        p.unwind("$properties");
        p.match("properties.name", prop);
        p.project("properties.keys");
        p.unwind("$properties.keys");

        Json cols = new Json();
        cols.put("_id", "$properties.keys._id");
        cols.put("name", "$properties.keys.name");
        cols.put("value", "$properties.keys.value");

        p.project(cols);

        List<Key> list = apps.aggregateAndCollect(p, Key.class);

        return list;
    }

}
