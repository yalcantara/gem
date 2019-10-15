package com.gem.commons.mongo;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import org.bson.BSON;
import org.bson.Transformer;

import java.util.ArrayList;
import java.util.List;

public class MongoUtils {


    private MongoUtils(){

    }

    public static List collect(MongoIterable iter){
        return collect(iter, Collection.DEFAULT_LIMIT);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List collect(MongoIterable iter, Integer limit) {
        List arr = new ArrayList<>();

        int capedLimit = (limit == null) ? Collection.DEFAULT_LIMIT : limit;
        try (MongoCursor cur = iter.iterator()) {
            int count = 0;

            // let us add another layer of safety by having a counter.
            while (cur.hasNext() && count < capedLimit) {
                count++;
                Object doc = cur.next();

                arr.add(doc);
            }
        }

        return arr;
    }



    public static <E extends Enum<E>> void addEnumHook(Class<E> clazz){
        BSON.addEncodingHook(clazz, new Transformer() {
            @Override
            public Object transform(Object objectToTransform) {
                if(objectToTransform == null){
                    return null;
                }

                return objectToTransform.toString();
            }
        });

        BSON.addDecodingHook(clazz, new Transformer() {
            @Override
            public Object transform(Object objectToTransform) {
                if(objectToTransform == null){
                    return null;
                }

                var val = (String)objectToTransform;
                var e = Enum.valueOf(clazz, val);

                return e;
            }
        });
    }
}
