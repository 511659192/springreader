// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.ResolvableType;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.StringJoiner;

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

    boolean constructorArgumentsResolved = false;


    boolean postProcessed = false;

    volatile boolean stale;

    @Nullable
    volatile Boolean isFactoryBean;

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
        return targetType != null ? ResolvableType.resolve(targetType) : null;
    }


    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
//        this.decoratedDefinition = original.decoratedDefinition;
//        this.qualifiedElement = original.qualifiedElement;
//        this.allowCaching = original.allowCaching;
//        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
//        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }

    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RootBeanDefinition.class.getSimpleName() + "[", "]").add("beforeInstantiationResolved=" + beforeInstantiationResolved)
                .add("resolvedTargetType=" + resolvedTargetType).add("targetType=" + targetType).add("constructorArgumentLock=" + constructorArgumentLock)
                .add("postProcessingLock=" + postProcessingLock).add("resolvedConstructorOrFactoryMethod=" + resolvedConstructorOrFactoryMethod).add("postProcessed=" + postProcessed).toString();
    }

    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        return null;
    }
}