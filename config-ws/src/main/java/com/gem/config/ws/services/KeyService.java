package com.gem.config.ws.services;

import com.gem.commons.Json;
import com.gem.commons.TxResult;
import com.gem.commons.Utils;
import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.PipeLine;
import com.gem.commons.mongo.Query;
import com.gem.commons.rest.ConflictException;
import com.gem.config.ws.entities.Key;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

import static com.gem.commons.Checker.checkParamNotNull;

@Service
public class KeyService {

    @Inject
    @Qualifier("apps")
    private Collection apps;

    @Inject
    private PropertyService propSrv;


    private void project(PipeLine p) {

        Json c = new Json();

        c.put("_id", 			"$keys._id");
        c.put("name", 			"$keys.name");
        c.put("value", 			"$keys.value");
        c.put("lastUpdate",		"$keys.lastUpdate");
        c.put("creationDate",	"$keys.creationDate");

        p.project(c);
    }

    private PipeLine pipe(String app, String prop){

        /*
        db.getCollection('apps').aggregate([

            {$match: {name: '911'}},
            {$project: {'properties': 1, _id: 0}},
        #1  {$unwind: '$properties'},
            {$match: {'properties.name': 'email'}},
            {$project: {'keys': '$properties.keys'}},
            {$unwind: '$keys'},
            {$project: {name: '$keys.name', value: '$keys.value'}}

        ]);
        */

        //propSrv.pipe solves until the #1 code in the comment above.
        PipeLine p = propSrv.pipe(app, true);
        p.match("name", prop);
        p.unwind("$keys");

        project(p);

        return p;
    }
    private PipeLine preProcess(String app, String prop){
        return preProcess(app, prop, true);
    }

    private PipeLine preProcess(String app, String prop, boolean checkPropExist){
        checkParamNotNull("app", app);
        checkParamNotNull("prop", prop);

        app = Verifier.checkId("app", app);
        prop = Verifier.checkId("prop", prop);

        if(checkPropExist){
            propSrv.checkExist(app, prop);
        }

        PipeLine p = pipe(app, prop);
        return p;
    }

    public List<Key> list(String app, String prop) {
        PipeLine p = preProcess(app, prop);

        List<Key> list = apps.aggregateAndCollect(p, Key.class);
        return list;
    }

    public Key get(String app, String prop, String name) {
        checkParamNotNull("name", name);

        name = Verifier.checkId("name", name);

        PipeLine p = preProcess(app, prop);
        p.match("name", name);

        List<Key> list = apps.aggregateAndCollect(p, Key.class);
        if (list.isEmpty() || list.get(0) == null) {
            throw new NotFoundException("The key '" + name + "' does not exist.");
        }

        Key dto = list.get(0);

        return dto;
    }


    public Key get(String app, String prop, ObjectId id) {
        checkParamNotNull("id", id);

        PipeLine p = preProcess(app, prop);
        p.match("_id", id);
        List<Key> list = apps.aggregateAndCollect(p, Key.class);
        if (list.isEmpty() || list.get(0) == null) {
            throw new NotFoundException("The key does not exist.");
        }

        Key dto = list.get(0);

        return dto;
    }

    public boolean exist(String app, String prop, String name) {
        checkParamNotNull("name", name);

        PipeLine p = preProcess(app, prop, false);
        p.match("name", name);
        long count = apps.count(p);

        return count > 0;
    }

    public void checkExist(String app, String prop, String name) {

        if (exist(app, prop, name) == false) {
            throw new BadRequestException("The key '" + name + "' does not exist.");
        }
    }

    public void checkIsAvailable(String app, String prop, String name) {

        if (exist(app, prop, name)) {
            throw new ConflictException("The key '" + name + "' already exists.");
        }
    }


    public Key create(String app, String prop, Key key) {
        checkParamNotNull("app", app);
        checkParamNotNull("prop", prop);
        checkParamNotNull("key.name", key.getName());

        String name = key.getName();

        app = Verifier.checkId("app", app);
        prop = Verifier.checkId("prop", prop);
        name = Verifier.checkId("name", name);

        propSrv.checkExist(app, prop);
        checkIsAvailable(app, prop, name);

        String value = Utils.strip(key.getValue());

        Key ent = new Key();
        ent.setId(new ObjectId());
        ent.setName(name);
        ent.setValue(value);

        Date now = new Date();
        ent.setCreationDate(now);
        ent.setLastUpdate(now);

        Query q = new Query();
        q.filter("name", app);
        q.filter("properties.name", prop);
        q.push("properties.$.keys", ent);

        long ans = apps.updateOne(q);

        if (ans != 1) {
            throw new ConflictException("The app or property have been previously modified.");
        }

        return ent;
    }

    public TxResult<Key> put(String app, String prop, ObjectId id, Key key) {
        checkParamNotNull("app", app);
        checkParamNotNull("prop", prop);
        checkParamNotNull("id", id);
        checkParamNotNull("key", key);
        checkParamNotNull("key.name", key.getName());

        String name = Verifier.checkId("name", key.getName());

        propSrv.checkExist(app, prop);

        Key ref = get(app, prop, id);
        if(ref.getName().equals(name) == false){
            // it's a rename, we have to check if the name is available
            checkIsAvailable(app, prop, name);
        }

        String value = Utils.strip(key.getValue());

        Query q = new Query();

        // The field properties is an array. But thanks to the filter, hopefully
        // it will bring just 1 property.
        q.update("properties.$[p].keys.$[k].name", name);

        Json k = new Json();
        k.put("k._id", id);

        Json p = new Json();
        p.put("p.name", prop);

        q.addArrayFilter("p", p);
        q.addArrayFilter("k", k);

        long count = apps.updateOne(q);

        if (count == 1) {
            Key ans = new Key();
            ans.setId(id);
            ans.setName(name);

            return new TxResult<>(false, ans);
        }

        throw new ConflictException("The property or key have been previously modified.");
    }

    public boolean delete(String app, String prop, String name) {
        checkParamNotNull("app", app);
        checkParamNotNull("prop", prop);
        checkParamNotNull("name", name);

        app = Verifier.checkId("app", app);
        prop = Verifier.checkId("prop", prop);
        name = Verifier.checkId("name", name);

        Query q = new Query();
        q.filter("name", app); // app
        q.filter("properties.name", prop);

        Json criteria = new Json();
        criteria.put("name", name);

        q.pull("properties.$.keys", criteria);

        long ans =  apps.updateOne(q);
        return ans > 0;
    }

}
