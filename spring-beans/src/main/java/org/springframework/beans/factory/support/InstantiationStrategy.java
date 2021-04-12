// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:49 下午
 **/
public interface InstantiationStrategy {
    Object instantiate(RootBeanDefinition mbd, String beanName, BeanFactory owner);
}