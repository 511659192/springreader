// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.config;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/7 21:30
 **/
public class AopConfigUtils {

    public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, Object source) {
        return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
    }

    private static BeanDefinition registerOrEscalateApcAsRequired(Class<?> cls, BeanDefinitionRegistry registry, Object source) {

        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            return null;
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
        beanDefinition.setSource(source);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", beanDefinition);
        return beanDefinition;
    }
}