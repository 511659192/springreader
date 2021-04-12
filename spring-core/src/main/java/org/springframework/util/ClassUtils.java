// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import java.lang.reflect.Constructor;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:24
 **/
public abstract class ClassUtils {

    public static <T> Class<T> forName(String className, boolean initializeBoolean, ClassLoader classLoader) {
        Class<T> aClass = null;
        try {
            aClass = (Class<T>) Class.forName(className, initializeBoolean, classLoader);
            return aClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            T t = declaredConstructor.newInstance();
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}