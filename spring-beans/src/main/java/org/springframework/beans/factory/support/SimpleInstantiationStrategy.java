// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:33 下午
 **/
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory owner) {
        return null;
    }

    @Override
    public Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory beanFactory, Constructor<?> constructorToUse, Object[] argsToUse) {
        if (!mbd.hasMethodOverrides()) {
            return ClassUtils.instantiateClass(constructorToUse, argsToUse);
        }

        return null;
    }
}