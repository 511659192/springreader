// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.StringJoiner;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:16
 **/
public class AnnotationTypeMapping {

    @Nullable
    private final AnnotationTypeMapping parentMapping;

    private final AnnotationTypeMapping root;

    @Getter
    private final int distance;

    @Getter
    private final Class<? extends Annotation> annotationType;

    @Nullable
    private final Annotation annotation;

    public <A extends Annotation> AnnotationTypeMapping(@Nullable AnnotationTypeMapping parentMapping, Class<? extends Annotation> annotationType, @Nullable Annotation annotation) {
        this.parentMapping = parentMapping;
        this.root = (parentMapping != null ? parentMapping.getRoot() : this);
        this.annotationType = annotationType;
        this.annotation = annotation;
        this.distance = parentMapping == null ? 0 : parentMapping.distance + 1;
    }

    AnnotationTypeMapping getRoot() {
        return this.root;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AnnotationTypeMapping.class.getSimpleName() + "[", "]").add("distance=" + distance).add("annotationType=" + annotationType).add("annotation=" + annotation)
                .toString();
    }
}