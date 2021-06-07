// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/7 21:54
 **/
public class BeanFactoryAdvisorRetrievalHelper {
    private ConfigurableListableBeanFactory beanFactory;

    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected boolean isEligibleBean(String beanName) {
        return true;
    }

}