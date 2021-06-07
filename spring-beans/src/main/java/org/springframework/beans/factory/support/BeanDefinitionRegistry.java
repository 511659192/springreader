// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:17 下午
 **/
public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    int getBeanDefinitionCount();

    boolean containsBeanDefinition(String className);

    void registerAlias(String name, String alias);

    BeanDefinition getBeanDefinition(String beanName);
}