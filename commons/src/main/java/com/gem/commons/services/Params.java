package com.gem.commons.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.gem.commons.Checker.checkParamNotNull;

public class Params {

    private final LinkedHashMap<String, Object> map;

    public Params(){
        map = new LinkedHashMap<>();
    }

    public Params set(String name, Object val){
        checkParamNotNull("name", name);
        map.put(name, val);
        return this;
    }

    public int size(){
        return map.size();
    }

    public Set<Map.Entry<String, Object>> entrySet(){
        return map.entrySet();
    }
}

