// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 11:00 上午
 **/
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(ConfigurableListableBeanFactory beanFactory);
}