// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.filter;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 11:27 上午
 **/
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {

    private final boolean considerMetaAnnotations;


    private Class<? extends Annotation> annotationType;

    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, true, false);
    }


    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces) {

        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }


    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {

        if (matchSelf(metadataReader)) {
            return true;
        }

        return false;
    }

    private boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        if (metadata.hasAnnotation(this.annotationType.getName())) {
            return true;
        }

        if (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName())) {
            return true;
        }

        return false;
    }
}