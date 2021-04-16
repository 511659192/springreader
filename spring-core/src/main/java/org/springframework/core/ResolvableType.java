// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core;

import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 8:25 下午
 **/
public class ResolvableType {

    @Getter
    private final Type type;
    @Getter
    private Class<?> resolved;

    public ResolvableType(Class<?> clazz) {
        this.type = clazz;
        this.resolved = clazz == null ? Object.class : clazz;
    }

    public static ResolvableType forClass(Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    @Nullable
    public Class<?> resolve() {
        return this.resolved;
    }

    public static Class<?> resolve(ResolvableType targetType) {
        return targetType.resolved;
    }

    public static ResolvableType forRawClass(Class<?> clazz) {
        return new ResolvableType(clazz) {

            @Override
            public boolean isAssignableFrom(Class<?> beanClass) {
                return beanClass == null || clazz.isAssignableFrom(beanClass);
            }
        };
    }

    public boolean isInstance(Object singleton) {
        return singleton != null && isAssignableFrom(singleton.getClass());
    }

    public boolean isAssignableFrom(Class<?> beanClass) {
        return resolved.isAssignableFrom(beanClass);
    }

    public boolean hasGenerics() {
        return (getGenerics().length > 0);
    }

    public ResolvableType[] getGenerics() {
        return new ResolvableType[0];
    }

    public Class<?> toClass() {
        return resolve(Object.class);
    }

    public Class<?> resolve(Class<?> fallback) {
        return (this.resolved != null ? this.resolved : fallback);
    }
}