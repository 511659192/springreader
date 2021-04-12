// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:16
 **/
public class AnnotationTypeMapping {

    @Nullable
    private final AnnotationTypeMapping source;

    @Getter
    private final int distance;

    @Getter
    private final Class<? extends Annotation> annotationType;

    @Nullable
    private final Annotation annotation;

    public <A extends Annotation> AnnotationTypeMapping(@Nullable AnnotationTypeMapping source,
            Class<? extends Annotation> annotationType, @Nullable Annotation annotation) {
        this.source = source;
        this.annotationType = annotationType;
        this.annotation = annotation;
        this.distance = source == null ? 0 : source.distance + 1;
    }
}