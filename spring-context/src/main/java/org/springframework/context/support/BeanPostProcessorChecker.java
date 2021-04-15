// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 11:11 上午
 **/
public class BeanPostProcessorChecker implements BeanPostProcessor {
    public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int count) {

    }
}