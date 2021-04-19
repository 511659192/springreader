// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.core.io.Resource;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:16 下午
 **/
@Getter
@Setter
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition, Cloneable {

    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    private String scope = SCOPE_SINGLETON;

    @Getter
    private volatile Class<?> beanClass;

    private String beanClassName;

    private String factoryBeanName;

    private String factoryMethodName;

    private String autowireMode;

    private String initMethodName;

    private boolean synthetic = false;

    private String beanName;

    @Nullable
    private MutablePropertyValues propertyValues;

    @Setter
    @Getter
    private Resource resource;
    private int role;
    private boolean abstractFlag;
    private Boolean lazyInit;
    private boolean autowireCandidate;
    private boolean primary = false;

    public AbstractBeanDefinition() {
    }

    public AbstractBeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.beanClassName = beanClass.getName();
    }

    public AbstractBeanDefinition(BeanDefinition original) {
        AbstractBeanDefinition from = ((AbstractBeanDefinition) original);
        this.scope = from.getScope();
        this.beanClass = from.getBeanClass();
        this.beanClassName = from.getBeanClassName();
        this.factoryBeanName = from.getFactoryBeanName();
        this.factoryMethodName = from.getFactoryMethodName();
        this.autowireMode = from.getAutowireMode();
        this.initMethodName = from.getInitMethodName();
        this.beanName = from.getBeanName();
    }

    @Override
    public boolean isSingleton() {
        return Objects.equals(this.scope, "singleton");
    }

    public Class<?> resolveBeanClass(ClassLoader beanClassLoader) {
        try {
            Class<?> beanClass = Class.forName(this.beanClassName, false, beanClassLoader);
            this.beanClass = beanClass;
            return beanClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRole(int role) {
        this.role = role;
    }


    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }


    @Override
    public MutablePropertyValues getPropertyValues() {
        return Optional.ofNullable(this.propertyValues).orElseGet(() -> new MutablePropertyValues());
    }

    public void applyDefaults(BeanDefinitionDefaults definitionDefaults) {

    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    @Override
    public boolean isLazyInit() {
        return (this.lazyInit != null && this.lazyInit.booleanValue());
    }

    @Override
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    public boolean isPrimary() {
        return this.primary;
    }
}