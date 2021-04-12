// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.ResolvableType;

import javax.annotation.Nullable;
import java.lang.reflect.Executable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 5:34 下午
 **/
public class RootBeanDefinition extends AbstractBeanDefinition {

    volatile Boolean beforeInstantiationResolved = Boolean.TRUE;

    volatile Class<?> resolvedTargetType;

    volatile ResolvableType targetType;

    final Object constructorArgumentLock = new Object();

    final Object postProcessingLock = new Object();

    Executable resolvedConstructorOrFactoryMethod;

    boolean postProcessed = false;

    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        super(beanClass);
    }

    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }

        ResolvableType targetType = this.targetType;
        return ResolvableType.resolve(targetType);
    }
}