// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.core.convert.ConversionService;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:23 下午
 **/
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

    int getBeanPostProcessorCount();

    void setConversionService(@Nullable ConversionService conversionService);

    boolean isFactoryBean(String name);
}