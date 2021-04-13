// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core;

import java.lang.reflect.Type;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 8:25 下午
 **/
public class ResolvableType {

    private final Type type;
    private Class<?> resolved;

    public ResolvableType(Type type) {
        this.type = type;
    }

    public static Class<?> resolve(ResolvableType targetType) {
        return targetType.resolved;
    }

    public static ResolvableType forRawClass(Class<?> typeToMatch) {
        return null;
    }

    public boolean isInstance(Object singleton) {
        return singleton != null && isAssignableFrom(singleton.getClass());
    }

    public boolean isAssignableFrom(Class<?> beanClass) {
        return resolved.isAssignableFrom(beanClass);
    }
}