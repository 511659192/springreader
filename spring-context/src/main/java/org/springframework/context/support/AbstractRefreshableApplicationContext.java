// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:48 下午
 **/
@Slf4j
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    @Setter
    @Getter
    private volatile DefaultListableBeanFactory beanFactory;

    public AbstractRefreshableApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {
        try {
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        } catch (Exception e) {
            log.error("error", e);
        }
    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception;

    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(null);
    }
}