// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 10:30 上午
 **/
public abstract class AnnotationUtils {
    public static boolean isCandidateClass(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        return isCandidateClass(beanClass, annotationType.getName());
    }

    public static boolean isCandidateClass(Class<?> beanClass, String annotationName) {
        if (annotationName.startsWith("java.")) {
            return true;
        }

        if (beanClass == Ordered.class) {
            return false;
        }

        return true;
    }

    public static boolean isCandidateClass(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes) {
        Predicate<Class<? extends Annotation>> predicate = annotationType -> isCandidateClass(clazz, annotationType);
        return annotationTypes.stream().filter(predicate).findAny().isPresent();
    }
}