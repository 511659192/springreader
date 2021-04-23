// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:11 下午
 **/
public interface BeanFactory {

    <T> T getBean(Class<T> classType, Object... args) throws BeansException;

    <T> T getBean(String beanName, Class<T> classType) throws BeansException;

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    default boolean isTypeMatch(String name, ResolvableType typeToMatch) {
        return isTypeMatch(name, typeToMatch.resolve());
    }

    boolean containsBean(String name);

    Object getBean(String name);

    Class<?> getType(String name);

    Class<?> getType(String name, boolean allowFactoryBeanInit);
}