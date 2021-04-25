// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 访问合并注解的集合,这些注解通常来自于 class 或者 method
 * 每一个合并注解提供一个视图,这个视图中的多个属性值可能通过不同的资源合并而来
 * 诸如:
 *  注解本身属性包含显式或者隐式的 AliasFor 声明
 *  显式为meta-annotation声明AliasFor
 *  meta-annotation注解别名转换
 *  meta-annotation注解本身
 * 举例:
 *  @PostMapping 注解可能被定义为以下形式
 *  public @interface PostMapping {
 *      @AliasFor(attibute = "path")
 *      String[] value() default {};
 *      @AliasFor(attibute = "value")
 *      string[] path() default {};
 *  }
 *
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 3:36 下午
 **/

public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {
    enum SearchStrategy {

        /**
         * Find only directly declared annotations, without considering
         * {@link Inherited @Inherited} annotations and without searching
         * superclasses or implemented interfaces.
         */
        DIRECT,
    }
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);

    static MergedAnnotations from(AnnotatedElement element) {
        return from(element, SearchStrategy.DIRECT);
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
        return from(element, searchStrategy, RepeatableContainers.standardRepeatables());
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers) {
        return from(element, searchStrategy, repeatableContainers, AnnotationFilter.PLAIN);
    }

    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        return TypeMappedAnnotations.from(element, searchStrategy, repeatableContainers, annotationFilter);
    }

    boolean isPresent(String annotationType);

    static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        return MergedAnnotationsCollection.of(annotations);
    }

    boolean isDirectlyPresent(String annotationName);

    <A extends Annotation> Optional<MergedAnnotation<A>> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate);

    /**
     * MergedAnnotationsCollection
     */
    class MergedAnnotationsCollection implements MergedAnnotations {

        private final MergedAnnotation<?>[] annotations;

        private final AnnotationTypeMappings[] mappings;

        public MergedAnnotationsCollection(Collection<MergedAnnotation<?>> annotations) {
            this.annotations = annotations.toArray(new MergedAnnotation[]{});
            this.mappings = new AnnotationTypeMappings[annotations.size()];
            for (int i = 0; i < annotations.size(); i++) {
                MergedAnnotation<?> annotation = this.annotations[i];
                this.mappings[i] = AnnotationTypeMappings.forAnnotationType(annotation.getType());
            }
        }

        private boolean isPresent(Object requiredType, boolean directOnly) {
            for (MergedAnnotation<?> annotation : this.annotations) {
                Class<? extends Annotation> type = annotation.getType();
                if (type == requiredType || type.getName().equals(requiredType)) {
                    return true;
                }
            }
            if (!directOnly) {
                for (AnnotationTypeMappings mappings : this.mappings) {
                    for (int i = 1; i < mappings.size(); i++) {
                        AnnotationTypeMapping mapping = mappings.get(i);
                        if (mapping.getAnnotationType().equals(requiredType)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
            return null;
        }

        @Override
        public boolean isPresent(String annotationType) {
            return isPresent(annotationType, false);
        }

        public static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
            return new MergedAnnotationsCollection(annotations);
        }

        @Override
        public boolean isDirectlyPresent(String annotationName) {
            return isPresent(annotationName, true);
        }

        @Override
        public <A extends Annotation> Optional<MergedAnnotation<A>> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {

            MergedAnnotationSelector<A> selector = (existing, candidate) -> (existing.getDistance() < candidate.getDistance()) ? existing : candidate;
            MergedAnnotation result = null;

            for (int i = 0; i < annotations.length; i++) {
                MergedAnnotation<?> root = annotations[i];
                AnnotationTypeMappings typeMappings = mappings[i];


                for (int idx = 0; idx < typeMappings.size(); idx++) {
                    AnnotationTypeMapping typeMapping = typeMappings.get(idx);
                    if (!isMappingForType(typeMapping, annotationType)) {
                        continue;
                    }

                    MergedAnnotation<A> candidate;
                    if (idx == 0) {
                        candidate = ((MergedAnnotation<A>) root);
                    } else {
                        candidate = TypeMappedAnnotation.createIfPossible(typeMapping, root);
                    }

                    if (candidate != null && (predicate != null || predicate.test(candidate))) {
                        result = (result == null) ? candidate : selector.select(result, candidate);
                    }
                }
            }

            return Optional.ofNullable(result);
        }

        private static boolean isMappingForType(AnnotationTypeMapping mapping, @Nullable Object requiredType) {
            if (requiredType == null) {
                return true;
            }
            Class<? extends Annotation> actualType = mapping.getAnnotationType();
            return (actualType == requiredType || actualType.getName().equals(requiredType));
        }

        @Override
        public Iterator<MergedAnnotation<Annotation>> iterator() {
            return Spliterators.iterator(spliterator());
        }

        @Override
        public Spliterator<MergedAnnotation<Annotation>> spliterator() {
            return new AnnotationsSpliterator<>(null);
        }


        private class AnnotationsSpliterator<A extends Annotation> implements Spliterator<MergedAnnotation<A>> {

            @Nullable
            private Object requiredType;

            private final int[] mappingCursors;

            public AnnotationsSpliterator(@Nullable Object requiredType) {
                this.mappingCursors = new int[annotations.length];
                this.requiredType = requiredType;
            }

            @Override
            public boolean tryAdvance(Consumer<? super MergedAnnotation<A>> action) {
                int lowestDistance = Integer.MAX_VALUE;
                int annotationResult = -1;
                for (int annotationIndex = 0; annotationIndex < annotations.length; annotationIndex++) {
                    AnnotationTypeMapping mapping = getNextSuitableMapping(annotationIndex);
                    if (mapping != null && mapping.getDistance() < lowestDistance) {
                        annotationResult = annotationIndex;
                        lowestDistance = mapping.getDistance();
                    }
                    if (lowestDistance == 0) {
                        break;
                    }
                }
                if (annotationResult != -1) {
                    MergedAnnotation<A> mergedAnnotation = createMergedAnnotationIfPossible(annotationResult, this.mappingCursors[annotationResult]);
                    this.mappingCursors[annotationResult]++;
                    if (mergedAnnotation == null) {
                        return tryAdvance(action);
                    }
                    action.accept(mergedAnnotation);
                    return true;
                }
                return false;
            }

            private MergedAnnotation<A> createMergedAnnotationIfPossible(int annotationIndex, int mappingIndex) {
                MergedAnnotation<?> root = annotations[annotationIndex];
                if (mappingIndex == 0) {
                    return (MergedAnnotation<A>) root;
                }

                AnnotationTypeMapping mapping = mappings[annotationIndex].get(mappingIndex);
                return new TypeMappedAnnotation<>(mapping, null, root.getSource(), null, null, mappingIndex);
            }

            private AnnotationTypeMapping getNextSuitableMapping(int annotationIndex) {
                int mappingCursor = mappingCursors[annotationIndex];
                return MergedAnnotationsCollection.this.mappings[annotationIndex].get(mappingCursor);
            }

            @Override
            public Spliterator<MergedAnnotation<A>> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return 0;
            }
        }
    }
}