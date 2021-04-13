// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 4:43 下午
 **/
public interface ConfigurableApplicationContext extends ApplicationContext {

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}