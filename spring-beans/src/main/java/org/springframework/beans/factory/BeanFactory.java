// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:11 下午
 **/
public interface BeanFactory {

    <T> T getBean(Class<T> classType) throws BeansException;

    <T> T getBean(String beanName, Class<T> classType) throws BeansException;

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    boolean containsBean(String name);
}