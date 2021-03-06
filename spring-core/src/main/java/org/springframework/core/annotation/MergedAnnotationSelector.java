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
        return true;
    }

    MergedAnnotation<A> select(MergedAnnotation<A> existing, MergedAnnotation<A> candidate);


    abstract class MergedAnnotationSelectors {

        private static final MergedAnnotationSelector<?> NEAREST = new MergedAnnotationSelector<Annotation>(){

            @Override
            public MergedAnnotation<Annotation> select(MergedAnnotation<Annotation> existing, MergedAnnotation<Annotation> candidate) {
                return null;
            }
        };


        public static <A extends Annotation> MergedAnnotationSelector<A> nearest() {
            return (MergedAnnotationSelector<A>) NEAREST;
        }

    }

}