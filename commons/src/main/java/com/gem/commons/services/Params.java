package com.gem.commons.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Params {

    private final Map<String, Object> map;

    public Params(){
        map = new LinkedHashMap<>();
    }

    public Params set(String name, Object val){
        map.put(name, val);
        return this;
    }


    public Set<Map.Entry<String, Object>> entrySet(){
        return map.entrySet();
    }
}

