// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:07
 **/
public class AnnotationTypeMappings<A extends Annotation> implements Iterable<AnnotationTypeMapping> {
    private final List<AnnotationTypeMapping> typeMappings = Lists.newArrayList();

    final Set<String> ignores = Sets.newHashSet("java.lang", "org.springframework.lang");

    public <A extends Annotation> AnnotationTypeMappings(Class<A> annotationType) {
        Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
        AnnotationTypeMapping annotationTypeMapping = new AnnotationTypeMapping(null, annotationType, null);
        queue.addLast(annotationTypeMapping);
        while (!queue.isEmpty()) {
            final AnnotationTypeMapping mapping = queue.removeFirst();
            this.typeMappings.add(mapping);
            addMetaAnnotationsToQueue(queue, mapping);
        }
    }

    private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping workingMapping) {
        final Class<? extends Annotation> annotationType = workingMapping.getAnnotationType();
        final Annotation[] declaredAnnotations = annotationType.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            if (ignores.stream().filter(ignore -> annotation.annotationType().getName().startsWith(ignore)).findAny().isPresent()) {
                continue;
            }
            queue.addLast(new AnnotationTypeMapping(workingMapping, annotation.annotationType(), annotation));
        }
    }

    public static <A extends Annotation> AnnotationTypeMappings forAnnotationType(Class<A> annotationType) {
        return new AnnotationTypeMappings(annotationType);
    }

    public AnnotationTypeMapping get(int i) {
        return this.typeMappings.get(i);
    }

    int size() {
        return this.typeMappings.size();
    }

    @Override
    public Iterator<AnnotationTypeMapping> iterator() {
        return this.typeMappings.iterator();
    }

    public Stream<AnnotationTypeMapping> stream() {
        return this.typeMappings.stream();
    }
}