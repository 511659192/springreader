// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.event;

import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 3:55 下午
 **/
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    @Setter
    private ConfigurableListableBeanFactory beanFactory;


    public SimpleApplicationEventMulticaster(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}