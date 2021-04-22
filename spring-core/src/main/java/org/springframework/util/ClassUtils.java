// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static String getClassFileName(Class<?> clazz) {
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(".");
        return className.substring(lastDotIndex + 1) + ".class";
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            return doInstantiateClass(declaredConstructor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiateClass(Constructor<T> constructor, Object... args) {
        try {
            return doInstantiateClass(constructor, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T doInstantiateClass(Constructor<T> constructor, Object... args)
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

    public static boolean isContainer(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    public static boolean isAssignable(Class<?> a, Class<?> b) {
        return a.isAssignableFrom(b) || b.isAssignableFrom(a);
    }

    public static Method[] getDeclaredMethods(Class<?> beanClass) {
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        Class<?>[] interfaces = beanClass.getInterfaces();
        if (interfaces.length == 0) {
            return declaredMethods;
        }

        Method[] ifcDefaultMethods = Arrays.stream(interfaces).flatMap(ifc -> {
            Method[] methods1 = ifc.getMethods();
            return Arrays.stream(methods1).filter(ifcMethod -> !Modifier.isAbstract(ifcMethod.getModifiers()));
        }).toArray(Method[]::new);

        if (ifcDefaultMethods.length == 0) {
            return declaredMethods;
        }

        Method[] result = new Method[declaredMethods.length + ifcDefaultMethods.length];

        System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
        System.arraycopy(ifcDefaultMethods, declaredMethods.length, result, declaredMethods.length, ifcDefaultMethods.length);
        return result;
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    public static <T> Constructor<T> getUserClassConstructors(Class<T> userClass, Class... paramTypes) {
        try {
            Constructor<T> declaredConstructor = userClass.getDeclaredConstructor(paramTypes);
            return declaredConstructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBeanClassShortName(Object obj) {
        String name;
        if (obj instanceof Class) {
            name = ((Class<?>) obj).getName();
        } else {
            name = obj.getClass().getName();
        }

        return getBeanClassShortName(name);
    }

    private static String getBeanClassShortName(String classFullName) {
        return classFullName.substring(classFullName.lastIndexOf(".") + 1);
    }
}