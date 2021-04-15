// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.io.Serializable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 11:02 上午
 **/
public class InitDestroyAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, PriorityOrdered, Serializable {

    private int order = Ordered.LOWEST_PRECEDENCE;

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }
}