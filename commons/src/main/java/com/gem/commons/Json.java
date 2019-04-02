package com.gem.commons;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class Json implements Iterable<String>, Serializable {

    private static final long serialVersionUID = -2237588925474667391L;

    private static final ObjectMapper plainMapper;

    private static final ObjectMapper mapper;
    private static final ObjectWriter writter;

    static {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        config(mapper);
        writter = mapper.writerWithDefaultPrettyPrinter();

        plainMapper = new ObjectMapper();
        config(plainMapper);
    }

    private static void config(ObjectMapper m) {
        SimpleModule mongo = new SimpleModule("Custom Mongo Module");
        mongo.addSerializer(ObjectId.class, ToStringSerializer.instance);
        m.registerModule(mongo);

        m.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        m.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        m.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        m.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }


    @SuppressWarnings("rawtypes")
    public static Json parse(String json) {

        try {

            LinkedHashMap map = mapper.readValue(json, LinkedHashMap.class);
            return new Json(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, Class<T> clazz) {

        try {

            T obj = mapper.readValue(json, clazz);
            return obj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(InputStream is, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        T obj = mapper.readValue(is, clazz);
        return obj;

    }

    @SuppressWarnings("rawtypes")
    public static Map parseAsMap(String json) {

        try {
            return mapper.readValue(json, LinkedHashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static String write(List list) {

        if (list == null) {
            return "null";
        }

        if (list.size() == 0) {
            return "[]";
        }

        try {
            return writter.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String write(Object obj) {

        try {
            return writter.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static String write(Map map) {

        if (map == null) {
            return "null";
        }

        if (map.isEmpty()) {
            return "{}";
        }

        try {
            return writter.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static String plainWrite(Map map) {

        if (map == null) {
            return "null";
        }

        if (map.isEmpty()) {
            return "{}";
        }

        try {

            return plainMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void plainWrite(OutputStream out, Map map)
            throws JsonProcessingException, IOException {

        if (map == null) {
            out.write("null".getBytes());
            return;
        }

        if (map.isEmpty()) {
            out.write("{}".getBytes());
            return;
        }

        plainMapper.writeValue(out, map);

    }

    public static List<Map<String, Object>> convert(List<Json> json) {
        List<Map<String, Object>> list = new ArrayList<>();

        json.stream().forEach(e -> list.add(e.toMap()));

        return list;
    }

    private final Map<String, Object> map;

    // private constructor, only for internal use.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Json(LinkedHashMap map) {
        this.map = map;
    }

    public Json() {
        map = new LinkedHashMap<>();
    }

    @SuppressWarnings("rawtypes")
    public Json(Map map) {
        this.map = new LinkedHashMap<>();

        for (Object raw : map.entrySet()) {
            Entry e = (Entry) raw;

            String key = String.valueOf(e.getKey());
            Object val = e.getValue();

            put(key, val);
        }
    }

    private boolean allowed(Object val) {
        if (val == null) {
            return true;
        }

        if (val instanceof String || val instanceof Number ||
                val instanceof ObjectId || val instanceof Json) {
            return true;
        }

        return false;
    }

    private void checkAllowed(Object val) {
        if(allowed(val)){
            return;
        }
        throw new IllegalArgumentException("Unsupported value type: " + val.getClass().getName());
    }

    public void put(String key, Object val) {
        checkAllowed(val);

        if (val == null) {
            put(key, (String) null);
            return;
        }

        if (val instanceof Integer) {
            put(key, (Integer) val);
            return;
        }

        if (val instanceof Long) {
            put(key, (Long) val);
            return;
        }

        if (val instanceof ObjectId) {
            put(key, (ObjectId) val);
            return;
        }

        if (val instanceof Json) {
            put(key, (Json) val);
            return;
        }


    }

    public void put(String key, Integer val) {
        checkParamNotNull("key", key);
        map.put(key, val);
    }

    public void put(String key, Long val) {
        checkParamNotNull("key", key);
        map.put(key, val);
    }

    public void put(String key, String val) {
        checkParamNotNull("key", key);
        map.put(key, val);
    }

    public void put(String key, Json val) {
        checkParamNotNull("key", key);
        map.put(key, val.toMap());
    }


    public void put(String key, ObjectId val) {
        checkParamNotNull("key", key);
        map.put(key, val);
    }

    public void put(String key, List arr) {
        checkParamNotNull("key", key);
        if (arr == null) {
            map.put(key, null);
        } else {
            List l = new ArrayList();
            for (Object e : arr) {
                checkAllowed(e);

                if(e instanceof Json){
                    l.add(((Json) e).toMap());
                }else{
                    l.add(e);
                }


            }
            map.put(key, l);
        }
    }

    public Integer getInt(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return (Integer) val;
    }

    public String getString(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return (String) val;
    }

    public Object getObject(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return val;
    }

    public Set<String> keys() {
        return map.keySet();
    }

    private void checkParamNotNull(String name, Object val) {
        if (val == null) {
            throw new NullPointerException("The parameter '" + name + "' can not be null.");
        }
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<String, Object>(map);
    }

    public Document toBson() {

        return Document.parse(write(map));
    }

    @Override
    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }

    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return Json.write(map);
    }
}
