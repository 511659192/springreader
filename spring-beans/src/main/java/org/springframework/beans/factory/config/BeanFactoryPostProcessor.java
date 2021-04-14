// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:24 上午
 **/
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}