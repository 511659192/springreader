// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:33 下午
 **/
public interface SingletonBeanRegistry {

    Object getSingleton(String beanName);

}