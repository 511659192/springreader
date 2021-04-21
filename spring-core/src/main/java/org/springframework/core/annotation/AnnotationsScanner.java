// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import org.springframework.core.Ordered;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 2:45 下午
 **/
abstract class AnnotationsScanner {

    @Nullable
    static <C, R> R scan(C context, AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy,
                         AnnotationsProcessor<C, R> processor) {

        R result = process(context, source, searchStrategy, processor);
        return processor.finish(result);
    }

    @Nullable
    private static <C, R> R process(C context, AnnotatedElement source,
                                    MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {

        if (source instanceof Class) {
            return processClass(context, (Class<?>) source, searchStrategy, processor);
        }
        if (source instanceof Method) {
            return processMethod(context, (Method) source, searchStrategy, processor);
        }
        return processElement(context, source, processor);
    }

    private static <R, C> R processElement(C context, AnnotatedElement source, AnnotationsProcessor<C, R> processor) {
        R result = processor.doWithAggregate(context, 0);

        if (result != null) {
            return result;
        }

        Annotation[] declaredAnnotations = getDeclaredAnnotations(source, false);
        return processor.doWithAnnotations(context, 0, source, declaredAnnotations);
    }

    private static <R, C> R processMethod(C context, Method source, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {
        return null;
    }

    private static <R, C> R processClass(C context, Class<?> source, MergedAnnotations.SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {
        return null;
    }

    public static boolean isKnownEmpty(AnnotatedElement element, MergedAnnotations.SearchStrategy searchStrategy) {
        if (hasPlainJavaAnnotationsOnly(element)) {
            return true;
        }

        if (searchStrategy != MergedAnnotations.SearchStrategy.DIRECT) {
            return false;
        }

        if (!isWithoutHierarchy(element, searchStrategy)) {
            return false;
        }

        if (element instanceof Method && ((Method) element).isBridge()) {
            return false;
        }

        return getDeclaredAnnotations(element, false).length == 0;
    }

    static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
        Annotation[] declaredAnnotations = source.getDeclaredAnnotations();
        return Arrays.stream(declaredAnnotations).filter(annotation -> {
            if (AnnotationFilter.PLAIN.matches(annotation)) {
                return false;
            }

            if (!AttributeMethods.forAnnotationType(annotation.annotationType()).isValid(annotation)) {
                return false;
            }

            return true;

        }).toArray(Annotation[]::new);
    }

    static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
        if (annotatedElement instanceof Class) {
            return hasPlainJavaAnnotationsOnly(((Class<?>) annotatedElement));
        }

        if (annotatedElement instanceof Member) {
            return hasPlainJavaAnnotationsOnly(((Member) annotatedElement).getDeclaringClass());
        }

        return false;
    }

    static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
        return (type.getName().startsWith("java.") || type == Ordered.class);
    }

    private static boolean isWithoutHierarchy(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (source == Object.class) {
            return true;
        }

        if (source instanceof Class) {
            Class<?> sourceClass = (Class<?>) source;
            boolean noSuper = sourceClass.getSuperclass() == Object.class && sourceClass.getInterfaces().length == 0;
            return noSuper;
        }

        if (source instanceof Method) {
            Method sourceMethod = (Method) source;
            return Modifier.isPrivate(sourceMethod.getModifiers()) || isWithoutHierarchy(sourceMethod.getDeclaringClass(), searchStrategy);
        }

        return true;
    }
}