// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:07 下午
 **/
@Slf4j
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

    @Setter
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver;


    public DefaultListableBeanFactory(BeanFactory parent) {
        super(parent);
        log.info("");
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    protected BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    @Override
    public boolean containsBeanDefinition(String className) {
        return this.beanDefinitionMap.containsKey(className);
    }

    @Override
    public void registerAlias(String name, String alias) {

    }

    @Override
    public <T> T getBean(Class<T> classType) throws BeansException {
        String beanName = getBeanNameForType(classType);
        return getBean(beanName, classType);
    }

    private <T> String getBeanNameForType(Class<T> classType) {
        return doGetBeanNameForType(classType)[0];
    }

    private <T> String[] doGetBeanNameForType(Class<T> classType) {
        String[] candidateBeanNames = this.beanDefinitionNames.stream().filter(beanName -> {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                return false;
            }
            boolean match = beanDefinition.getBeanClassName().equals(classType.getName());
            return match;
        }).toArray(String[]::new);
        return candidateBeanNames;

    }

    private <T> boolean isTypeMatch(String beanName, Class<T> classType, boolean allowFactoryBeanInit) {
        Object singleton = getSingleton(beanName);
        return classType.isInstance(singleton);
    }
}