// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 4:39 下午
 **/
public abstract class BeanUtils {

    public static <T> T instantiateClass(Constructor<T> constructor, Object... args) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}