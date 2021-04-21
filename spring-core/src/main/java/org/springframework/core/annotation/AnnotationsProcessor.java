// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 4:10 下午
 **/
@FunctionalInterface
public interface AnnotationsProcessor<C, R> {

    @Nullable
    R doWithAnnotations(C context, int aggregateIndex, @Nullable Object source, Annotation[] annotations);

    @Nullable
    default R finish(@Nullable R result) {
        return result;
    }

    default R doWithAggregate(C context, int aggregateIndex) {
        return null;
    }
}