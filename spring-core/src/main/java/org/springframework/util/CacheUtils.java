// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/23 3:57 下午
 **/
public class CacheUtils {

    public static <K, V> CacheBuilder newBuilder() {
        return CacheBuilder.<K, V>newBuilder();
    }

    public static <K, V> V get(Cache<K, V> cache, K key,  Callable<V> callable) {
        try {
            return cache.get(key, callable);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}