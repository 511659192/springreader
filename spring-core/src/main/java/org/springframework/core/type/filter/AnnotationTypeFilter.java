// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.filter;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.annotation.Annotation;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 11:27 上午
 **/
public class AnnotationTypeFilter implements TypeFilter {
    private Class<? extends Annotation> annotationType;

    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
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

        return false;
    }


}