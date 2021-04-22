// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
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

import static org.springframework.util.ClassUtils.getBeanClassShortName;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:36 下午
 **/
@Slf4j
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    @Nullable
    private TypeConverter typeConverter;
    @Getter
    private boolean cacheBeanMetadata = true;

    @Getter
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
    @Setter
    private ConversionService conversionService;

    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null, null, false);
    }

    @Override
    public boolean isFactoryBean(String name) {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance instanceof FactoryBean) {
            return true;
        }

        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
        }
        RootBeanDefinition beanDefinition = getMergedLocalBeanDefinition(beanName);
        return isFactoryBean(beanName, beanDefinition);
    }

    private boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Boolean isFactoryBean = mbd.isFactoryBean;
        if (isFactoryBean != null) {
            return isFactoryBean;
        }
        Class<?> resolveBeanClass = predictBeanType( mbd);
        boolean result = resolveBeanClass != null && FactoryBean.class.isAssignableFrom(resolveBeanClass);
        mbd.isFactoryBean = result;
        return result;
    }


    protected Class<?> predictBeanType(RootBeanDefinition mbd) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        Class<?> resolveBeanClass = resolveBeanClass(mbd);
        return resolveBeanClass;
    }


    public boolean isTypeMatch(String name, ResolvableType typeToMatch, boolean allowFactoryBeanInit) {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName);
        if (beanInstance != null) {
            return typeToMatch.isInstance(beanInstance);
        }

        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            return parentBeanFactory.isTypeMatch(name, typeToMatch);
        }

        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        Class<?> classToMatch = typeToMatch.resolve();
        Class<?>[] typesToMatch = new Class[]{FactoryBean.class, classToMatch};

        Class<?> predictedType = predictBeanType(beanName, mbd, typesToMatch);
        return typeToMatch.isAssignableFrom(predictedType);
    }

    private Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>[] typesToMatch) {

        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }

        if (StringUtils.isNotBlank(mbd.getFactoryMethodName())) {
            return null;
        }
        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    private Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?>... typesToMatch) {
        if (mbd.hasBeanClass()) {
            return mbd.getBeanClass();
        }

        return doResolveBeanClass(mbd, typesToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        ResolvableType resolvableType = ResolvableType.forRawClass(typeToMatch);
        return isTypeMatch(name, resolvableType, false);
    }

    @Override
    public <T> T getBean(String name, Class<T> classType) throws BeansException {
        return doGetBean(name, classType, null, false);
    }

    public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args) {
        return doGetBean(name, requiredType, args, false);
    }

    private <T> T doGetBean(String name, Class<T> classType, @Nullable Object[] args, boolean typeCheckOnly) {
        String beanName = transformedBeanName(name);

        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance == null || args != null) {
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            if (mbd.isSingleton()) {
                sharedInstance = getSingleton(beanName, () -> createBean(beanName, mbd));
            } else {
                // todo not support
            }
        }

        Object beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        T adaptBeanInstance = adaptBeanInstance(name, beanInstance, classType);
        return adaptBeanInstance;
    }

    <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> clazz) {
        if (clazz == null || clazz.isInstance(bean)) {
            return (T) bean;
        }

        TypeConverter typeConverter = getTypeConverter();
        Object convertedBean = typeConverter.convertIfNecessary(bean, clazz);
        if (convertedBean == null) {
            throw new RuntimeException("bean not fount" + name);
        }
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

    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch) {
        ClassLoader beanClassLoader = getBeanClassLoader();
        return mbd.resolveBeanClass(beanClassLoader);
    }

    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object... args);

    protected String transformedBeanName(String name) {
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
        return getMergedBeanDefinition(beanName, beanDefinition, null);
    }

    private RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition beanDefinition, @Nullable BeanDefinition containingBd) {
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mergedBeanDef = this.mergedBeanDefinitions.get(beanName);
            if (mergedBeanDef == null || mergedBeanDef.stale) {
                if (beanDefinition.getParentName() == null) {
                    if (beanDefinition instanceof RootBeanDefinition) {
                        mergedBeanDef = ((RootBeanDefinition) beanDefinition).cloneBeanDefinition();
                    } else {
                        mergedBeanDef = new RootBeanDefinition(beanDefinition);
                    }
                } else {
                    BeanDefinition parentBeanDefinition;
                    String parentBeanName = transformedBeanName(beanDefinition.getParentName());
                    if (!beanName.equals(parentBeanName)) {
                        parentBeanDefinition = getMergedLocalBeanDefinition(parentBeanName);
                    } else {
                        BeanFactory parentBeanFactory = getParentBeanFactory();
                        if (parentBeanFactory instanceof ConfigurableBeanFactory) {
                            parentBeanDefinition = ((ConfigurableBeanFactory) parentBeanFactory).getMergedBeanDefinition(parentBeanName);
                        } else {
                            throw new RuntimeException("not support");
                        }
                    }

                    mergedBeanDef = new RootBeanDefinition(parentBeanDefinition);
                    mergedBeanDef.overrideFrom(beanDefinition);
                }

                if (containingBd == null && isCacheBeanMetadata()) {
                    this.mergedBeanDefinitions.put(beanName, mergedBeanDef);
                }
            }

            return mergedBeanDef;
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
        log.info("customEditor:{}", getBeanClassShortName(registry));
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        this.propertyEditorRegistrars.add(registrar);
        log.info(" register:{}", getBeanClassShortName(registrar));
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
        log.info(" refresh processor:{}", getBeanClassShortName(beanPostProcessor));
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return containsSingleton(name) || this.containsBeanDefinition(beanName);
     }

    @Override
    public boolean containsBean(String name) {
        if (containsLocalBean(name)) {
            return true;
        }

        if (this.parentBeanFactory != null) {
            return this.parentBeanFactory.containsBean(name);
        }

        return false;
    }

    @Override
    @Nullable
    public Class<?> getType(String name) {
        return getType(name, true);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) {
        String beanName = transformedBeanName(name);

        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean && !name.startsWith("$")) {
                return ((FactoryBean) beanInstance).getObjectType();
            }
            return beanInstance.getClass();
        }

        return null;
    }

    @Override
    public int getBeanPostProcessorCount() {
        return CollectionUtils.size(this.beanPostProcessors);
    }

    public abstract boolean containsBeanDefinition(String className);


    @Override
    public BeanDefinition getMergedBeanDefinition(String beanName) {
        beanName = transformedBeanName(beanName);

        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }

        return getMergedLocalBeanDefinition(beanName);
    }

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


    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {

    }

}