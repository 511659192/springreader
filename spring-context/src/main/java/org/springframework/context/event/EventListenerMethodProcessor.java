// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextAware;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/20 4:40 下午
 **/
@Slf4j
public class EventListenerMethodProcessor implements SmartInitializingSingleton, ApplicationContextAware, BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        log.info("");
    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}