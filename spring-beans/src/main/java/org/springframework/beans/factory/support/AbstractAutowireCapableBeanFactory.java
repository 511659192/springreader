// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.ParameterNameDiscoverer;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.util.ClassUtils.getBeanClassShortName;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:35 下午
 **/
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    @Setter
    @Getter
    private InstantiationStrategy instantiationStrategy;
    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
    private boolean allowCircularReferences = true;
    private boolean allowRawInjectionDespiteWrapping = false;
    @Getter
    private ParameterNameDiscoverer parameterNameDiscoverer = new ParameterNameDiscoverer.DefaultParameterNameDiscoverer();

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);

        ignoreDependencyInterface(BeanNameAware.class);
        ignoreDependencyInterface(BeanFactoryAware.class);
        ignoreDependencyInterface(BeanClassLoaderAware.class);

        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
//        log.info("");
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

        Object o = doCreateBean(beanName, mbd, args);
        return o;
    }

    private Object doCreateBean(String beanName, RootBeanDefinition mbd, Object... args) {
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }

        if (instanceWrapper == null) {
            // 创建实例
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }

        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }

        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            }
            mbd.postProcessed = true;
        }

        boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
        if (earlySingletonExposure) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
        }


        Object exposedObject = bean;
        populateBean(beanName, mbd, instanceWrapper);
        exposedObject = initializingBean(beanName, exposedObject, mbd);

        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
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
//            log.info("processorName: {} beanName: {}", getBeanClassShortName(beanPostProcessor), getBeanClassShortName(existingBean));
            Object current = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }


    private void invokeAwareMethods(String beanName, Object bean) {
        if (!(bean instanceof Aware)) {
            return;
        }

        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory((ConfigurableListableBeanFactory) this);
        }


    }

    private void populateBean(String beanName, RootBeanDefinition mbd, BeanWrapper beanWrapper) {
        List<InstantiationAwareBeanPostProcessor> instAwareBpps = getBeanPostProcessorCache().instantiationAware;
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            for (InstantiationAwareBeanPostProcessor bp : instAwareBpps) {
                if (!bp.postProcessAfterInstantiation(beanWrapper.getWrappedInstance(), beanName)) {
                    return;
                }
            }
        }

        PropertyValues pvs = mbd.getPropertyValues();
        int resolvedAutowireMode = mbd.getResolvedAutowireMode();
        if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
            autowireByName(beanName, mbd, beanWrapper, pvs);
        }

        if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
            autowireByType(beanName, mbd, beanWrapper, pvs);
        }

        for (InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor : instAwareBpps) {
            PropertyValues pvsToUse = instantiationAwareBeanPostProcessor.postProcessProperties(pvs, beanWrapper.getWrappedInstance(), beanName);
            pvs = pvsToUse;
        }

        applyPropertyValues(beanName, mbd, beanWrapper, pvs);

    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        if (pvs == null || pvs.isEmpty()) {
            return;
        }
    }

    private void autowireByType(String beanName, RootBeanDefinition mbd, BeanWrapper beanWrapper, PropertyValues newPvs) {

    }

    private void autowireByName(String beanName, RootBeanDefinition mbd, BeanWrapper beanWrapper, PropertyValues newPvs) {

    }

    private void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanClass, String beanName) {
        List<MergedBeanDefinitionPostProcessor> mergedDefinition = this.getBeanPostProcessorCache().mergedDefinition;
        for (MergedBeanDefinitionPostProcessor processor : mergedDefinition) {
//            log.info("processorName: {} beanName: {}", getBeanClassShortName(processor), getBeanClassShortName(beanClass));
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

        if (ArrayUtils.isEmpty(args)) {
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

        if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR || mbd.hasConstructorArgumentValues() || ArrayUtils.isNotEmpty(args)) {
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
        Object using = bean;
        List<BeanPostProcessor> beanPostProcessors = this.getBeanPostProcessors();
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
//            log.info("processorName: {} beanName: {}", getBeanClassShortName(beanPostProcessor), getBeanClassShortName(bean));
            Object current = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            if (current == null) {
                return using;
            }

            using = current;
        }

        return using;
    }

    private Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (InstantiationAwareBeanPostProcessor beanPostProcessor : getBeanPostProcessorCache().instantiationAware) {
//            log.info("processorName: {} beanName: {}", getBeanClassShortName(beanPostProcessor), getBeanClassShortName(beanClass));
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
        ConstructorResolver constructorResolver = new ConstructorResolver(this);
        BeanWrapper beanWrapper = constructorResolver.autowireConstructor(beanName, mbd, ctors, explicitArgs);
        return beanWrapper;
    }


    public Object instantiate(RootBeanDefinition mbd, String beanName, Constructor<?> constructorToUse, Object[] argsToUse) {
        return getInstantiationStrategy().instantiate(mbd, beanName, this, constructorToUse, argsToUse);
    }
}