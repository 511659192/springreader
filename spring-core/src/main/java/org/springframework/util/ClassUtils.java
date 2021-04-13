// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:24
 **/
public abstract class ClassUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, false);
        values.put(byte.class, (byte) 0);
        values.put(short.class, (short) 0);
        values.put(int.class, 0);
        values.put(long.class, (long) 0);
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
    }

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
            return instantiateClass(declaredConstructor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiateClass(Constructor<T> constructor, Object... args)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        constructor.setAccessible(true);

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Preconditions.checkArgument(args.length == parameterTypes.length);
        Object[] argsWithDefaultValues = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                Class<?> parameterType = parameterTypes[i];
                argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
            } else {
                argsWithDefaultValues[i] = args[i];
            }
        }

        T t = constructor.newInstance(argsWithDefaultValues);
        return t;
    }

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}