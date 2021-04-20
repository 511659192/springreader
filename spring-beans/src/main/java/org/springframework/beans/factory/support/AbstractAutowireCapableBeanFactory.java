// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:35 下午
 **/
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    @Setter
    private InstantiationStrategy instantiationStrategy;
    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
    private boolean allowCircularReferences = true;
    private boolean allowRawInjectionDespiteWrapping = false;

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);

        ignoreDependencyInterface(BeanNameAware.class);
        ignoreDependencyInterface(BeanFactoryAware.class);
        ignoreDependencyInterface(BeanClassLoaderAware.class);

        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
        log.info("");
    }


    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object... args) {
        RootBeanDefinition mbdToUse = mbd;

        Class<?> resolveBeanClass = resolveBeanClass(mbdToUse);
        if (resolveBeanClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolveBeanClass);
        }


        mbdToUse.prepareMethodOverrides();

        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }

        return doCreateBean(beanName, mbd, args);
    }

    private Object doCreateBean(String beanName, RootBeanDefinition mbd, Object... args) {
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }

        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }

        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }

        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                applyMergedBeanDefinitionPostPorcessors(mbd, beanType, beanName);
            }
            mbd.postProcessed = true;
        }

        boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
        if (earlySingletonExposure) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
        }


        Object exposedObject = bean;
        polulateBean(beanName, mbd, instanceWrapper);
        exposedObject = initializingBean(beanName, exposedObject, mbd);

        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonExposure;
                } else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)){

                }
            }
        }

        registerDisposableBeanIfNecessary(beanName, bean, mbd);
        return exposedObject;
    }

    private boolean hasDependentBean(String beanName) {
        return false;
    }

    private Object initializingBean(String beanName, Object bean, RootBeanDefinition mbd) {
        invokeAwareMethods(beanName, bean);
        Object wrappedBean = bean;
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        invokeInitMethod(beanName, wrappedBean, mbd);
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    private void invokeInitMethod(String beanName, Object wrappedBean, RootBeanDefinition mbd) {

    }

    private Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {

        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : this.getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }


    private void invokeAwareMethods(String beanName, Object bean) {
    }

    private void polulateBean(String beanName, RootBeanDefinition mbd, BeanWrapper wrapper) {

        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            List<InstantiationAwareBeanPostProcessor> instantiationAware = getBeanPostProcessorCache().instantiationAware;
            for (InstantiationAwareBeanPostProcessor bp : instantiationAware) {
                if (!bp.postProcessAfterInstantiation(wrapper.getWrappedInstance(), beanName)) {
                    return;
                }
            }
        }
    }

    private void applyMergedBeanDefinitionPostPorcessors(RootBeanDefinition mbd, Class<?> beanClass, String beanName) {
        for (MergedBeanDefinitionPostProcessor processor : this.getBeanPostProcessorCache().mergedDefinition) {
            processor.postProcessMergedBeanDefinition(mbd, beanClass, beanName);
        }
    }


    private BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object... args) {
        Class<?> beanClass = resolveBeanClass(mbd);

        if (mbd.getFactoryMethodName() != null) {
            // todo
        }

        boolean resolved = false;
        boolean autowireNecessary = false;

        if (args.length == 0) {
            synchronized (mbd.constructorArgumentLock) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }

        if (resolved) {
            if (autowireNecessary) {
                return autowireConstructor(beanName, mbd, null, null);
            }

            return instantiateBean(beanName, mbd);
        }

        Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);

        if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR || mbd.hasConstructorArgumentValues() || args.length != 0) {
            return autowireConstructor(beanName, mbd, ctors, args);
        }

        ctors = mbd.getPreferredConstructors();
        if (ctors != null) {
            return autowireConstructor(beanName, mbd, ctors, null);
        }

        return instantiateBean(beanName, mbd);
    }

    private Constructor<?>[] determineConstructorsFromBeanPostProcessors(Class<?> beanClass, String beanName) {
        if (beanClass == null) {
            return null;
        }

        if (!hasInstantiationAwareBeanPostProcessors()) {
            return null;
        }

        List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = getBeanPostProcessorCache().smartInstantiationAware;
        for (SmartInstantiationAwareBeanPostProcessor bp : smartInstantiationAware) {
            Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
            if (ctors != null) {
                return ctors;
            }
        }

        return null;
    }

    private BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        Object beanInstance = this.instantiationStrategy.instantiate(mbd, beanName, this);
        BeanWrapper beanWrapper = new BeanWrapperImpl(beanInstance);
        initBeanWrapper(beanWrapper);
        return beanWrapper;
    }


    private Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbdToUse) {
        if (!mbdToUse.beforeInstantiationResolved) {
            return null;
        }

        if (mbdToUse.isSynthetic()) {
            return null;
        }

        if (!hasInstantiationAwareBeanPostProcessors()) {
            return null;
        }

        Class<?> targetType = determineTargetType(beanName, mbdToUse);

        if (targetType == null) {
            return null;
        }

        Object bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
        if (bean == null) {
            return null;
        }
        bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        return bean;

    }

    private Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        log.info("beanName:{}", beanName);
        Object using = bean;
        List<BeanPostProcessor> beanPostProcessors = this.getBeanPostProcessors();
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            log.info("processorName:{}", beanPostProcessor.getClass().getName());
            Object current = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            if (current == null) {
                return using;
            }

            using = current;
        }

        return using;
    }

    private Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        log.info("beanName:{}", beanName);

        for (InstantiationAwareBeanPostProcessor beanPostProcessor : getBeanPostProcessorCache().instantiationAware) {
            log.info("processorName:{}", beanPostProcessor.getClass().getName());
            Object bean = beanPostProcessor.postProcessBeforeInstantiation(beanClass, beanName);
            if (bean != null) {
                return bean;
            }
        }

        return null;
    }

    protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }

        String factoryMethodName = mbd.getFactoryMethodName();
        if (StringUtils.isNotBlank(factoryMethodName)) {
            return getTypeForFactoryMethod(beanName, mbd, targetType);
        }

        return resolveBeanClass(mbd);
    }

    private Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... targetType) {
        return null;
    }



    public void ignoreDependencyInterface(Class<?> type) {
        this.ignoredDependencyInterfaces.add(type);
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposeObj = bean;
        if (mbd.isSynthetic()) {
            return exposeObj;
        }

        if (!hasInstantiationAwareBeanPostProcessors()) {
            return exposeObj;
        }

        for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
            exposeObj = bp.getEarlyBeanReference(exposeObj, beanName);
        }

        return exposeObj;
    }

    protected BeanWrapper autowireConstructor(
            String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {


        return null;
    }
}