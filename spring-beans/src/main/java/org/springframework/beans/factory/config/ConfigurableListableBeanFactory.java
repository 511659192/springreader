// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ListableBeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:08 下午
 **/
public interface ConfigurableListableBeanFactory extends ConfigurableBeanFactory, ListableBeanFactory {

    void ignoreDependencyInterface(Class<?> type);
}