// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 3:36 下午
 **/
public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {

    boolean isPresent(String annotationType);

    static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        return MergedAnnotationsCollection.of(annotations);
    }

    boolean isDirectlyPresent(String annotationName);

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