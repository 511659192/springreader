// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:04
 **/
final class TypeMappedAnnotation<A extends Annotation> extends AbstractMergedAnnotation<A> {

    private final AnnotationTypeMapping mapping;
    @Nullable
    private final ClassLoader classLoader;
    @Nullable
    @Getter
    private final Object source;
    @Nullable
    private final Object rootAttributes;
    private final ValueExtractor valueExtractor;
    private final int aggregateIndex;

    public TypeMappedAnnotation(AnnotationTypeMapping mapping, @Nullable ClassLoader classLoader, @Nullable Object source, @Nullable Object rootAttributes, ValueExtractor valueExtractor,
            int aggregateIndex) {
        this.mapping = mapping;
        this.classLoader = classLoader;
        this.source = source;
        this.rootAttributes = rootAttributes;
        this.valueExtractor = valueExtractor;
        this.aggregateIndex = aggregateIndex;
    }

    static <A extends Annotation> MergedAnnotation<A> of(@Nullable ClassLoader classLoader, @Nullable Object source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
        AnnotationTypeMappings mappings = AnnotationTypeMappings.forAnnotationType(annotationType);
        ValueExtractor valueExtractor = TypeMappedAnnotation::extractFromMap;
        return new TypeMappedAnnotation<>(mappings.get(0), classLoader, source, attributes, valueExtractor, 0);
    }

    @Nullable
    static Object extractFromMap(Method attribute, @Nullable Object map) {
        return (map != null ? ((Map<String, ?>) map).get(attribute.getName()) : null);
    }

    @Override
    public Class<A> getType() {
        return (Class<A>) this.mapping.getAnnotationType();
    }
}