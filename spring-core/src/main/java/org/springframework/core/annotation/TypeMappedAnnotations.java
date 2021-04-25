// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 2:25 下午
 **/
public class TypeMappedAnnotations implements MergedAnnotations{

    static final MergedAnnotations NONE = new TypeMappedAnnotations(null, null, null, AnnotationFilter.ALL);


    private final AnnotatedElement element;
    private final SearchStrategy searchStrategy;
    private final RepeatableContainers repeatableContainers;
    private final AnnotationFilter annotationFilter;
    @Nullable
    private final Annotation[] annotations;
    private final Object source;

    public TypeMappedAnnotations(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        this.source = element;
        this.element = element;
        this.searchStrategy = searchStrategy;
        this.repeatableContainers = repeatableContainers;
        this.annotationFilter = annotationFilter;
        this.annotations = null;
    }

    public static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy, RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {
        if (AnnotationsScanner.isKnownEmpty(element, searchStrategy)) {
            return NONE;
        }
        
        return new TypeMappedAnnotations(element, searchStrategy, repeatableContainers, annotationFilter);
    }

    
    @Override
    public boolean isPresent(String annotationType) {
        return false;
    }

    @Override
    public boolean isDirectlyPresent(String annotationName) {
        return false;
    }

    @Override
    public <A extends Annotation> Optional<MergedAnnotation<A>> get(String annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate) {
        return Optional.empty();
    }

    @Override
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return null;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
        return get(annotationType, null, null);
    }

    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, @Nullable Predicate<? super MergedAnnotation<A>> predicate,
                                                          @Nullable MergedAnnotationSelector<A> selector) {
        if (this.annotationFilter.matches(annotationType)) {
            return MergedAnnotation.MISSING;
        }

        MergedAnnotationFinder<A> finder = new MergedAnnotationFinder<>(annotationType, predicate, selector);
        MergedAnnotation<A> result = scan(annotationType, finder);
        return Optional.ofNullable(result).orElse(MergedAnnotation.MISSING);
    }

    @Nullable
    private <C, R> R scan(C criteria, AnnotationsProcessor<C, R> processor) {
        if (this.annotations != null) {
            R result = processor.doWithAnnotations(criteria, 0, this.source, this.annotations);
            return processor.finish(result);
        }

        if (this.element != null && this.searchStrategy != null) {
            return AnnotationsScanner.scan(criteria, this.element, this.searchStrategy, processor);
        }

        return null;
    }


    private static boolean isMappingForType(AnnotationTypeMapping mapping, AnnotationFilter annotationFilter, @Nullable Object requiredType) {
        final Class<? extends Annotation> annotationType = mapping.getAnnotationType();
        if (annotationFilter.matches(annotationType)) {
            return false;
        }

        return Objects.equals(annotationType, requiredType);
    }

    private class MergedAnnotationFinder<A extends Annotation> implements AnnotationsProcessor<Object, MergedAnnotation<A>> {

        private final Object requiredType;
        @Nullable
        private final Predicate<? super MergedAnnotation<A>> predicate;
        @Nullable
        private final MergedAnnotationSelector<A> selector;

        @Nullable
        private MergedAnnotation<A> result;

        MergedAnnotationFinder(Object requiredType, @Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector) {
            this.requiredType = requiredType;
            this.predicate = predicate;
            this.selector = Optional.ofNullable(selector).orElseGet(MergedAnnotationSelector.MergedAnnotationSelectors::nearest);
        }

        @Nullable
        @Override
        public MergedAnnotation<A> doWithAnnotations(Object context, int aggregateIndex, @Nullable Object source, Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (annotation == null) {
                    continue;
                }
                if (annotationFilter.matches(annotation)) {
                    continue;
                }

                final MergedAnnotation<A> result = this.process(context, aggregateIndex, source, annotation);
                return result;
            }


            return null;
        }

        @Override
        public MergedAnnotation<A> doWithAggregate(Object context, int aggregateIndex) {
            return this.result;
        }

        @Nullable
        @Override
        public MergedAnnotation<A> finish(@Nullable MergedAnnotation<A> result) {
            return Optional.ofNullable(result).orElse(this.result);
        }

        @Nullable
        private MergedAnnotation<A> process(Object type, int aggregateIndex, @Nullable Object source, Annotation annotation) {
            final AnnotationTypeMappings<A> mappings = AnnotationTypeMappings.forAnnotationType(annotation.annotationType());

            final Iterator<AnnotationTypeMapping> iterator = mappings.iterator();
            while (iterator.hasNext()) {
                final AnnotationTypeMapping mapping = iterator.next();
                if (!TypeMappedAnnotations.isMappingForType(mapping, TypeMappedAnnotations.this.annotationFilter, this.requiredType)) {
                    continue;
                }

                final MergedAnnotation<A> candidate = TypeMappedAnnotation.createIfPossible(mapping, source, annotation, aggregateIndex);
                if (candidate == null) {
                    continue;
                }

                if (this.predicate != null && !this.predicate.test(candidate)) {
                    continue;
                }

                if (this.selector.isBestCandidate(candidate)) {
                    return candidate;
                }
            }
            return null;
        }
    }
}