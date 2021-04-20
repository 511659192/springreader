// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type;

import org.springframework.core.annotation.MergedAnnotation;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:10 下午
 **/
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

    default boolean hasAnnotatedMethods(String annotationName) {
        return !getAnnotatedMethods(annotationName).isEmpty();
    }

    Set<MethodMetadata> getAnnotatedMethods(String annotationName);


    default boolean hasAnnotation(String annotationName) {
        return getAnnotations().isDirectlyPresent(annotationName);
    }

    default boolean hasMetaAnnotation(String metaAnnotationName) {
        Predicate<MergedAnnotation<Annotation>> predicate = MergedAnnotation::isMetaPresent;
        return getAnnotations().get(metaAnnotationName, predicate).isPresent();
    }
}