// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.core.io.Resource;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_NO;


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

    public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;
    public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
    public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
    public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;


    private String scope = SCOPE_SINGLETON;

    @Getter
    @Setter
    private volatile Class<?> beanClass;

    @Setter
    private String beanClassName;

    private String factoryBeanName;

    private String factoryMethodName;

    @Setter
    private int autowireMode = AUTOWIRE_NO;

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

    @Setter
    private Boolean lazyInit;
    private boolean autowireCandidate;
    private boolean primary = false;

    @Nullable
    private ConstructorArgumentValues constructorArgumentValues;

    @Getter
    private MethodOverrides methodOverrides = new MethodOverrides();

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

    public void overrideFrom(BeanDefinition other) {
        setLazyInit(other.isLazyInit());

        if (StringUtils.isNotBlank(other.getBeanClassName())) {
            setBeanClassName(other.getBeanClassName());
        }

        // todo  other
    }

    public void prepareMethodOverrides() {

    }

    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            // Work out whether to apply setter autowiring or constructor autowiring.
            // If it has a no-arg constructor it's deemed to be setter autowiring,
            // otherwise we'll try constructor autowiring.
            Constructor<?>[] constructors = getBeanClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        }
        else {
            return this.autowireMode;
        }
    }

    @Override
    public boolean hasConstructorArgumentValues() {
        return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty());
    }

    @Nullable
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return Optional.of(this.constructorArgumentValues).orElseGet(() -> new ConstructorArgumentValues());
    }

}