// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:24 上午
 **/
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);
}