// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.convert;

import org.springframework.core.ResolvableType;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/16 4:00 下午
 **/
public class TypeDescriptor implements Serializable {

    private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);

    private static final Class<?>[] CACHED_COMMON_TYPES = {
            boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class,
            double.class, Double.class, float.class, Float.class, int.class, Integer.class,
            long.class, Long.class, short.class, Short.class, String.class, Object.class};

    static {
        for (Class<?> type : CACHED_COMMON_TYPES) {
            commonTypesCache.put(type, valueOf(type));
        }
    }

    private final ResolvableType resolvableType;
    private final Class<? extends Object> type;
    private final AnnotatedElementAdapter annotatedElement;

    public TypeDescriptor(ResolvableType resolvableType, @Nullable Class<?> type, @Nullable Annotation[] annotations) {
        this.resolvableType = resolvableType;
        this.type = (type != null ? type : resolvableType.toClass());
        this.annotatedElement = new AnnotatedElementAdapter(annotations);
    }

    public static TypeDescriptor valueOf(Class<?> clazz) {
        if (clazz == null) {
            clazz = Object.class;
        }

        TypeDescriptor typeDescriptor = commonTypesCache.get(clazz);
        if (typeDescriptor != null) {
            return typeDescriptor;
        }

        return new TypeDescriptor(ResolvableType.forClass(clazz), null, null);
    }

    private class AnnotatedElementAdapter implements AnnotatedElement, Serializable {

        @Nullable
        private final Annotation[] annotations;

        public AnnotatedElementAdapter(@Nullable Annotation[] annotations) {
            this.annotations = annotations;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return new Annotation[0];
        }
    }
}