// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.management.MemoryPoolMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 3:21 下午
 **/
@Slf4j
public class AttributeMethods {

    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);

    private final Class<? extends Annotation> annotationType;
    private final Method[] attributeMethods;

    public AttributeMethods(Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
    }

    public static AttributeMethods forAnnotationType(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }

        return compute(annotationType);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] declaredMethods = annotationType.getDeclaredMethods();
        Predicate<Method> isAttributeMethod = method -> method.getParameterCount() == 0 && method.getReturnType() != void.class;
        Method[] attributeMethods = Arrays.stream(declaredMethods).filter(isAttributeMethod).toArray(Method[]::new);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    boolean isValid(Annotation annotation) {
        return true;
    }
}