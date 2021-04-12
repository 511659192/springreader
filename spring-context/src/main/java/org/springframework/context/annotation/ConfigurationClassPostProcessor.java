// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:23 上午
 **/
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }
}