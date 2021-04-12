// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:07 下午
 **/
public interface MetadataReader {

    Resource getResource();

    ClassMetadata getClassMetadata();

    AnnotationMetadata getAnnotationMetadata();
}