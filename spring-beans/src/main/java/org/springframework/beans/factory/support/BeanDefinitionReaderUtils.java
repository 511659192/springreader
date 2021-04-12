// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 3:46 下午
 **/
public class BeanDefinitionReaderUtils {

    public static void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry) {
        String beanName = beanDefinitionHolder.getBeanName();
        registry.registerBeanDefinition(beanName, beanDefinitionHolder.getBeanDefinition());

        for (String alias : beanDefinitionHolder.getAliases()) {
            registry.registerAlias(beanName, alias);
        }
    }
}