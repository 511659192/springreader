// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:33 下午
 **/
public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy {

    public CglibSubclassingInstantiationStrategy() {
    }

    @Override
    public Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory owner) {
        try {
            Constructor<?> declaredConstructor;
            synchronized (mbd.constructorArgumentLock) {
                Class<?> beanClass = mbd.getBeanClass();
                declaredConstructor = beanClass.getDeclaredConstructor();

                mbd.resolvedConstructorOrFactoryMethod = declaredConstructor;
            }

            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}