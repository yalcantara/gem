package com.gem.commons;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

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
        SimpleModule gemModule = new SimpleModule("The GEM Jackson Module");
        gemModule.addSerializer(ObjectId.class, new ToStringSerializer());
        gemModule.addSerializer(Json.class, new JsonSerializer());
        gemModule.addDeserializer(Json.class, new JsonDeserializer());
        m.registerModule(gemModule);

        m.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        m.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        m.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        m.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }



    private static class JsonSerializer extends StdSerializer<Json>{

        protected JsonSerializer() {
            super(Json.class);
        }

        @Override
        public void serialize(Json value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(value.map);
        }
    }

    private static class JsonDeserializer extends StdDeserializer<Json> {

        protected JsonDeserializer() {
            super(Json.class);
        }

        @Override
        public Json deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
            ObjectCodec codec = p.getCodec();

            LinkedHashMap map = codec.readValue(p, LinkedHashMap.class);
            return new Json(map);
        }


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
    public static String plainWrite(Object obj) {

        if (obj == null) {
            return "null";
        }

        try {

            return plainMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void plainWrite(OutputStream out, Object obj)
            throws JsonProcessingException, IOException {

        if (obj == null) {
            out.write("null".getBytes());
            return;
        }


        plainMapper.writeValue(out, obj);

    }

    public static List<Map<String, Object>> convert(List<Json> json) {
        List<Map<String, Object>> list = new ArrayList<>();

        json.stream().forEach(e -> list.add(e.toMap()));

        return list;
    }

    public static <T> List<Json> toListOfJson(List<T> list, Function<T, Json> mapper){
        checkParamNotNull("list", list);
        checkParamNotNull("mapper", mapper);

        List<Json> ans = new ArrayList<>();

        if(list instanceof RandomAccess){
            for(int i =0; i < list.size(); i++){
                ans.add(mapper.apply(list.get(i)));
            }
        }else{
            for(T t:list){
                ans.add(mapper.apply(t));
            }
        }


        return ans;
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

    private Json(Map map){
        this(map, true);
    }

    @SuppressWarnings("rawtypes")
    private Json(Map map, boolean copy) {
        if(copy){
            this.map = new LinkedHashMap<>();
            for (Object raw : map.entrySet()) {
                Entry e = (Entry) raw;

                String key = String.valueOf(e.getKey());
                Object val = e.getValue();

                put(key, val);
            }
        }else{
            this.map = map;
        }
    }

    private boolean allowed(Object val) {
        if (val == null) {
            return true;
        }

        if (val instanceof String || val instanceof Number ||
                val instanceof ObjectId || val instanceof Json || val instanceof Instant) {
            return true;
        }

        return false;
    }

    private void checkAllowed(Object val) {
        if (allowed(val)) {
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
        if (val == null) {
            map.put(key, null);
        } else {
            map.put(key, val.toMap());
        }
    }

    public void put(String key, Instant val) {
        Date date = (val == null)?null:Date.from(val);
        put(key, date);
    }

    public void put(String key, Date val) {
        checkParamNotNull("key", key);
        map.put(key, val);
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

                if (e instanceof Json) {
                    l.add(((Json) e).toMap());
                } else if(e instanceof Instant){
                    l.add(Date.from((Instant)e));
                }else {
                    l.add(e);
                }
            }
            map.put(key, l);
        }
    }


    public boolean containsKey(String key){
        checkParamNotNull("key", key);
        return map.containsKey(key);
    }

    public boolean isField(String key, boolean val){
        if(containsKey(key) == false){
            return false;
        }
        Object a = getBoolean(key);
        if(a == null){
            return true;
        }

        return a.equals(val);
    }

    public Integer getInt(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        if(val == null){
            return null;
        }
        return ((Number) val).intValue();
    }

    public Long getLong(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        if(val == null){
            return null;
        }
        return ((Number) val).longValue();
    }

    public Long getDouble(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        if(val == null){
            return null;
        }
        return ((Number) val).longValue();
    }

    public String getString(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return (String) val;
    }


    public Boolean getBoolean(String key){
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return (Boolean) val;
    }

    public Object getObject(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return val;
    }

    public Json getJson(String key){
        checkParamNotNull("key", key);
        Map val = (Map)map.get(key);
        return new Json(val, false);
    }

    public List getList(String key) {
        checkParamNotNull("key", key);
        Object val = map.get(key);
        return (List) val;
    }

    public Set<String> keys() {
        return map.keySet();
    }

    private static void checkParamNotNull(String name, Object val) {
        if (val == null) {
            throw new NullPointerException("The parameter '" + name + "' can not be null.");
        }
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(map);
    }

    public Document toBson() {

        Document doc = new Document();

        for(Entry e:map.entrySet()){
            String key = (String)e.getKey();
            Object val = e.getValue();

            doc.put(key, val);
        }

        return doc;
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
