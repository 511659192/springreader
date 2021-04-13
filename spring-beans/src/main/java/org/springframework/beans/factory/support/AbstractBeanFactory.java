// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:36 下午
 **/
@Slf4j
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    @Nullable
    private TypeConverter typeConverter;

    private BeanFactory parentBeanFactory;

    private Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

    public AbstractBeanFactory(BeanFactory parentBeanFactory) {
        log.info("");
        this.parentBeanFactory = parentBeanFactory;
    }

    private BeanPostProcessorCache beanPostProcessorCache;

    private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

    @Getter
    private final List<BeanPostProcessor> beanPostProcessors = new BeanPostProcessorCacheAwareList();

    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet<>(4);
    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>(4);

    @Getter
    private ConversionService conversionService;

    public boolean isTypeMatch(String name, ResolvableType typeToMatch, boolean allowFactoryBeanInit) {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName);
        if (beanInstance != null) {
            return typeToMatch.isInstance(beanInstance);
        }

        RootBeanDefinition rootBeanDefinition = getMergedLocalBeanDefinition(beanName);
        Class<?> beanClass = rootBeanDefinition.getBeanClass();
        return typeToMatch.isAssignableFrom(beanClass);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        ResolvableType resolvableType = ResolvableType.forRawClass(typeToMatch);
        return isTypeMatch(name, resolvableType, false);
    }

    @Override
    public <T> T getBean(String name, Class<T> classType) throws BeansException {
        return doGetBean(name, classType);
    }

    private <T> T doGetBean(String name, Class<T> classType) {
        String beanName = transformedBeanName(name);
        Object bean = null;

        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null) {
        } else {
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            if (mbd.isSingleton()) {
                sharedInstance = getSingleton(beanName, () -> createBean(beanName, mbd));
            }
        }

        bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);

        return (T) bean;
    }

    <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
        if (requiredType == null || requiredType.isInstance(bean)) {
            return (T) bean;
        }

        TypeConverter typeConverter = getTypeConverter();
        Object convertedBean = typeConverter.convertIfNecessary(bean, requiredType);
        return (T) convertedBean;
    }

    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }
        else {
            // Build default TypeConverter, registering custom editors.
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            typeConverter.setConversionService(getConversionService());
            registerCustomEditors(typeConverter);
            return typeConverter;
        }
    }

    @Nullable
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    protected Class<?> resolveBeanClass(RootBeanDefinition mbd) {
        if (mbd.getBeanClass() != null) {
            return mbd.getBeanClass();
        }

        return doResolveBeanClass(mbd);
    }

    private Class<?> doResolveBeanClass(RootBeanDefinition mbd) {
        ClassLoader beanClassLoader = getBeanClassLoader();
        return mbd.resolveBeanClass(beanClassLoader);
    }

    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object... args);

    private String transformedBeanName(String name) {
        return name;
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {
        return beanInstance;
    }


    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) {
        RootBeanDefinition mergedBeanDefinition = this.mergedBeanDefinitions.get(beanName);
        if (mergedBeanDefinition != null) {
            return mergedBeanDefinition;
        }

        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return getMergedBeanDefinition(beanName, beanDefinition);
    }

    private RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mdb = this.mergedBeanDefinitions.get(beanName);
            if (mdb == null) {
                mdb = new RootBeanDefinition(beanDefinition);
                this.mergedBeanDefinitions.put(beanName, mdb);
            }
            return mdb;
        }
    }

    private ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName);

    protected boolean hasInstantiationAwareBeanPostProcessors() {
        BeanPostProcessorCache beanPostProcessorCache = getBeanPostProcessorCache();
        return CollectionUtils.isNotEmpty(beanPostProcessorCache.instantiationAware);
    }


    BeanPostProcessorCache getBeanPostProcessorCache() {
        BeanPostProcessorCache cache = this.beanPostProcessorCache;
        if (cache != null) {
            return cache;
        }

        cache = new BeanPostProcessorCache();
        for (BeanPostProcessor processor : this.beanPostProcessors) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                cache.instantiationAware.add((InstantiationAwareBeanPostProcessor) processor);
            }

            if (processor instanceof SmartInstantiationAwareBeanPostProcessor) {
                cache.smartInstantiationAware.add((SmartInstantiationAwareBeanPostProcessor) processor);
            }

            if (processor instanceof MergedBeanDefinitionPostProcessor) {
                cache.mergedDefinition.add((MergedBeanDefinitionPostProcessor) processor);
            }
        }

        this.beanPostProcessorCache = cache;
        return cache;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = Optional.ofNullable(beanClassLoader).orElseGet(() -> ClassUtils.getDefaultClassLoader());
    }

    protected void initBeanWrapper(BeanWrapper beanWrapper) {
        beanWrapper.setConversionService(this.getConversionService());
        registerCustomEditors(beanWrapper);
    }

    private void registerCustomEditors(PropertyEditorRegistry registry) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            ((PropertyEditorRegistrySupport) registry).useConfigValueEditors();
        }

        for (PropertyEditorRegistrar registrar : propertyEditorRegistrars) {
            registrar.registerCustomEditors(registry);
        }

        this.customEditors.forEach((beanType, editorClass) -> registry.registerCustomEditor(beanType, BeanUtils.instantiateClass(editorClass)));
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        this.propertyEditorRegistrars.add(registrar);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return containsSingleton(name) || this.containsBeanDefinition(beanName);
     }

    public abstract boolean containsBeanDefinition(String className);

    /**
     * Internal cache of pre-filtered post-processors.
     *
     * @since 5.3
     */
    static class BeanPostProcessorCache {

        final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList<>();

        final List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = new ArrayList<>();

        final List<MergedBeanDefinitionPostProcessor> mergedDefinition = new ArrayList<>();
    }

    private class BeanPostProcessorCacheAwareList extends CopyOnWriteArrayList<BeanPostProcessor> {

    }
}