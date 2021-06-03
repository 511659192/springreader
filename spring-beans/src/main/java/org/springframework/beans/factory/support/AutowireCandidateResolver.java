// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:22 上午
 **/
public interface AutowireCandidateResolver {
    default boolean isAutowireCandidate(BeanDefinitionHolder holder, DependencyDescriptor descriptor) {
        return holder.getBeanDefinition().isAutowireCandidate();
    }
}