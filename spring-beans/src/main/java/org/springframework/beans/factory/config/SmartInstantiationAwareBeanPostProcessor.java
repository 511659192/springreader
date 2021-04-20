// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 11:38 上午
 **/
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {


    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }

    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
            throws BeansException {

        return null;
    }
}