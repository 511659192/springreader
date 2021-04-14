// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;
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

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

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
        prepareBeanFactory(beanFactory);
        postProcessBeanFactory(beanFactory); // no use
        invokeBeanFactoryPostProcessors(beanFactory);

    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.postProcessBeanFactory(beanFactory, getBeanFactoryPostProcessors());
    }

    private void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        ClassLoader classLoader = getClassLoader();
        beanFactory.setBeanClassLoader(classLoader);

        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        beanFactory.ignoreDependencyInterface(Aware.class);

        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
//        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);


        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", getEnvironment());
        }
        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", getEnvironment().getSystemEnvironment());
        }
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

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

    @Override
    public boolean containsLocalBean(String name) {
        return getBeanFactory().containsLocalBean(name);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletions, boolean allowEagerInit) {
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletions, allowEagerInit);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }
}