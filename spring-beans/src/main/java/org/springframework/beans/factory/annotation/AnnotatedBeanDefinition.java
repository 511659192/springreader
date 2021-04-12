// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 9:07 下午
 **/
public interface AnnotatedBeanDefinition extends BeanDefinition {

    AnnotationMetadata getMetadata();
}