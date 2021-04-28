// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import lombok.Getter;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 适配根注解的属性值,通过AnnotationTypeMapping的mapping或者mirroring来获取
 *
 * 跟属性值从源对象,通过回调获取.这样同一个类会存在多种不同的注解模型
 *
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
    @Getter
    private final Object rootAttributes;
    @Getter
    private final ValueExtractor valueExtractor;
    @Getter
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
        TypeMappedAnnotation<A> typeMappedAnnotation = new TypeMappedAnnotation<>(mappings.get(0), classLoader, source, attributes, valueExtractor, 0);
        return typeMappedAnnotation;
    }

    @Nullable
    static Object extractFromMap(Method attribute, @Nullable Object map) {
        return (map != null ? ((Map<String, ?>) map).get(attribute.getName()) : null);
    }

    public static <A extends Annotation> MergedAnnotation<A> createIfPossible(AnnotationTypeMapping typeMapping, MergedAnnotation<?> annotation) {
        if (annotation instanceof TypeMappedAnnotation) {
            TypeMappedAnnotation<?> root = (TypeMappedAnnotation<?>) annotation;
            return createIfPossible(typeMapping, root.source, root.rootAttributes, root.valueExtractor, root.aggregateIndex);
        }

        return null;
    }

    @Nullable
    static <A extends Annotation> TypeMappedAnnotation<A> createIfPossible(AnnotationTypeMapping mapping, @Nullable Object source, Annotation annotation, int aggregateIndex) {
        return createIfPossible(mapping, source, annotation, ClassUtils::invokeMethod, aggregateIndex);
    }

    private static <A extends Annotation> TypeMappedAnnotation<A> createIfPossible(AnnotationTypeMapping typeMapping, Object source, Object rootAttributes, ValueExtractor valueExtractor, int aggregateIndex) {
        return new TypeMappedAnnotation<>(typeMapping, null, source, rootAttributes, valueExtractor, aggregateIndex);
    }

    @Override
    public Class<A> getType() {
        return (Class<A>) this.mapping.getAnnotationType();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public int getDistance() {
        return this.mapping.getDistance();
    }
}