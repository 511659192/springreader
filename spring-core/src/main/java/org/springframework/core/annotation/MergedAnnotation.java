// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 22:38
 **/
public interface MergedAnnotation<T extends Annotation> {

    static <T extends Annotation> MergedAnnotation<T> of(ClassLoader classLoader, Object source, Class<T> annotationType, Map<String, Object> attributes) {
        return TypeMappedAnnotation.of(classLoader, source, annotationType, attributes);
    }

    Class<T> getType();

    @Nullable
    Object getSource();
}