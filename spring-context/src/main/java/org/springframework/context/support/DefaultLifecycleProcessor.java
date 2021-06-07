// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.LifecycleProcessor;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 5:44 下午
 **/
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {

    @Nullable
    private volatile ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void onRefresh() {

    }
}