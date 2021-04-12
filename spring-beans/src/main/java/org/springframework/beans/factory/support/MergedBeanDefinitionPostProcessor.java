// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 11:39 上午
 **/
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

    /**
     *
     * @param beanDefinition
     * @param beanType
     * @param beanName
     */
    void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);
}