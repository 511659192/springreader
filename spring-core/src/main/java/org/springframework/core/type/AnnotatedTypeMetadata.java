// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type;

import org.springframework.core.annotation.MergedAnnotations;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:12 下午
 **/
public interface AnnotatedTypeMetadata {

    default boolean isAnnotated(String annotationName) {
        return getAnnotations().isPresent(annotationName);
    }

    MergedAnnotations getAnnotations();

}