// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Optional;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 4:43 下午
 **/
@Slf4j
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    private ResourcePatternResolver resourcePatternResolver;

    private ApplicationContext parent;

    @Setter
    private ConfigurableEnvironment environment;

    public AbstractApplicationContext() {
        log.info("");
        ResourceLoader resourceLoader = this;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    public AbstractApplicationContext(ApplicationContext parent) {
        this();
        this.parent = parent;
    }

    public void refresh() throws BeansException, IllegalStateException {
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;


    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public <T> T getBean(Class<T> classType) throws BeansException {
        return getBeanFactory().getBean(classType);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> classType) throws BeansException {
        return getBeanFactory().getBean(beanName, classType);
    }

    public ConfigurableEnvironment getEnvironment() {
        log.info("");
        ConfigurableEnvironment environment = Optional.ofNullable(this.environment).orElseGet(() -> new StandardEnvironment());
        this.environment = environment;
        return environment;
    }

    @Override
    public Resource[] getResources(String locationPattern) {
        log.info("locationPattern:{}", locationPattern);
        return this.resourcePatternResolver.getResources(locationPattern);
    }
}