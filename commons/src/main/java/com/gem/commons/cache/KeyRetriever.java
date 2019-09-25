package com.gem.commons.cache;

@FunctionalInterface
public interface KeyRetriever<K, V> {

    public V retrieve(K key)throws Exception;
}
