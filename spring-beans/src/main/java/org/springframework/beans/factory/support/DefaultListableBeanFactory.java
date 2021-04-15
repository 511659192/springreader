// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
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
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);


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
        return getBeanNamesForType(classType, true, false)[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletions, boolean allowEagerInit) {
        return doGetBeanNameForType(type, includeNonSingletions, allowEagerInit);
    }

    private <T> String[] doGetBeanNameForType(Class<T> targetType, boolean includeNonSingletions, boolean allowEagerInit) {
        List<String> beanDefinitionNames = this.beanDefinitionNames;
        return beanDefinitionNames.stream().filter(beanName -> {
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            boolean isFactoryBean = isFactoryBean(beanName, mbd);
            boolean matchFound = false;
            if (!isFactoryBean) {
                matchFound = isTypeMatch(beanName, targetType, allowEagerInit);
            }
            return matchFound;
        }).toArray(String[]::new);
    }

    protected Class<?> predictBeanType(RootBeanDefinition mbd) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        Class<?> resolveBeanClass = resolveBeanClass(mbd);
        return resolveBeanClass;
    }

    private boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Boolean isFactoryBean = mbd.isFactoryBean;
        if (isFactoryBean != null) {
            return isFactoryBean;
        }
        Class<?> resolveBeanClass = predictBeanType( mbd);
        boolean resultl = resolveBeanClass != null && FactoryBean.class.isAssignableFrom(resolveBeanClass);
        mbd.isFactoryBean = resultl;
        return resultl;
    }

    private <T> boolean isTypeMatch(String beanName, Class<T> typeToMatch, boolean allowFactoryBeanInit) {
        Object singleton = getSingleton(beanName);
        if (singleton != null) {
            return typeToMatch.isInstance(singleton);
        }


        RootBeanDefinition rootBeanDefinition = getMergedLocalBeanDefinition(beanName);
        Class<?> beanClass = rootBeanDefinition.getBeanClass();
        return typeToMatch.isAssignableFrom(beanClass);
    }

    @Override
    public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue))) {
                throw new IllegalArgumentException(
                        "Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        super.registerSingleton(beanName, singletonObject);
    }

    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) {
        return getBeansOfType(type, true, false);
    }

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) {
        Map<String, T> result = Maps.newHashMap();

        String[] beanNamesForType = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        for (String beanName : beanNamesForType) {
            T bean = getBean(beanName, type);
            result.put(beanName, bean);
        }

        return result;
    }

    @Override
    public void preInstantiateSingletons() {

    }
}