// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Getter
    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    @Nullable
    @Getter
    private ApplicationEventMulticaster applicationEventMulticaster;

    @Getter
    private LifecycleProcessor lifecycleProcessor;

    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;

    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    public AbstractApplicationContext() {
        log.info("");
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(((ResourceLoader) this));
    }

    public AbstractApplicationContext(ApplicationContext parent) {
        this();
        this.parent = parent;
        if (parent != null) {
            Environment parentEnv = parent.getEnvironment();
            if (parentEnv instanceof ConfigurableEnvironment) {
                getEnvironment().merge(parentEnv);
            }
        }
    }

    public void refresh() throws BeansException, IllegalStateException {
        prepareRefresh();

        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        prepareBeanFactory(beanFactory);
        postProcessBeanFactory(beanFactory); // no use
        invokeBeanFactoryPostProcessors(beanFactory);
        registerBeanPostProcessors(beanFactory);

        initApplicationEventMulticaster();
        onRefresh(); // do nothing

        registerListeners();

        finishBeanFactoryInitialization(beanFactory);

        finishRefresh();
    }

    private void prepareRefresh() {
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = Sets.newHashSet(this.applicationListeners);
        } else {
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }

        this.earlyApplicationEvents = Sets.newHashSet();
    }

    private void finishRefresh() {
        initLifecycleProcessor();

        getLifecycleProcessor().onRefresh();
    }

    private void initLifecycleProcessor() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean("lifecycleProcessor")) {
            this.lifecycleProcessor = beanFactory.getBean("lifecycleProcessor", LifecycleProcessor.class);
        } else {
            DefaultLifecycleProcessor defaultLifecycleProcessor = new DefaultLifecycleProcessor();
            defaultLifecycleProcessor.setBeanFactory(beanFactory);
            this.lifecycleProcessor = defaultLifecycleProcessor;
        }
    }

    private void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsLocalBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
            beanFactory.setConversionService(beanFactory.getBean("conversionService", ConversionService.class));
        }

        beanFactory.preInstantiateSingletons();
    }

    private void registerListeners() {
        Set<ApplicationListener<?>> applicationListeners = getApplicationListeners();
        for (ApplicationListener<?> applicationListener : applicationListeners) {
            getApplicationEventMulticaster().addApplicationListener(applicationListener);
        }

        String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
        for (String listenerBeanName : listenerBeanNames) {
            getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }
    }

    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
            this.applicationEventMulticaster = beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
        } else {
            this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
            beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);
        }
    }

    private void onRefresh() {
        // todo do nothing
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
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
    public <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(beanName, requiredType);
    }

    public ConfigurableEnvironment getEnvironment() {
//        log.info("");
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
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletions, boolean allowEagerInit) {
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletions, allowEagerInit);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return getBeanFactory().getBean(requiredType, args);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }
}