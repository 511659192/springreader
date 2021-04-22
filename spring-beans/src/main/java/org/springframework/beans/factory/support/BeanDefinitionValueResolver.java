// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.TypeConverter;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/22 7:41 下午
 **/
public class BeanDefinitionValueResolver {

    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final String beanName;
    private final RootBeanDefinition mbd;
    private final TypeConverter converter;

    public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName, RootBeanDefinition mbd, TypeConverter converter) {

        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.mbd = mbd;
        this.converter = converter;
    }
}