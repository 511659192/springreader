// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/20 3:01 下午
 **/
@FunctionalInterface
public interface MergedAnnotationSelector<A extends Annotation> {

    default boolean isBestCandidate(MergedAnnotation<A> annotation) {
        return false;
    }

    MergedAnnotation<A> select(MergedAnnotation<A> existing, MergedAnnotation<A> candidate);
}