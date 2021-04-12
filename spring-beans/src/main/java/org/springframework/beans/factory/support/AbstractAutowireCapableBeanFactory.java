// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:35 下午
 **/
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    @Setter
    private InstantiationStrategy instantiationStrategy;

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        log.info("");
        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
    }


    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object... args) {
        RootBeanDefinition mbdToUse = mbd;

        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }

        return doCreateBean(beanName, mbd, args);
    }

    private Object doCreateBean(String beanName, RootBeanDefinition mbd, Object... args) {
        BeanWrapper wrapper = createBeanInstance(beanName, mbd, args);
        Object bean = wrapper.getWrappedInstance();
        Class<?> beanClass = wrapper.getWrappedClass();

        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                applyMergedBeanDefinitionPostPorcessors(mbd, beanClass, beanName);
            }
            mbd.postProcessed = true;
        }

        polulateBean(beanName, mbd, wrapper);
        return initializingBean(beanName, bean, mbd);
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

    }

    private void applyMergedBeanDefinitionPostPorcessors(RootBeanDefinition mbd, Class<?> beanClass, String beanName) {
        for (MergedBeanDefinitionPostProcessor processor : this.getBeanPostProcessorCache().mergedDefinition) {
            processor.postProcessMergedBeanDefinition(mbd, beanClass, beanName);
        }
    }

    private BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object... args) {
        Class<?> beanClass = resolveBeanClass(mbd, beanName);

        if (mbd.getFactoryMethodName() != null) {
            // todo
        }


        return instantiateBean(beanName, mbd);
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

        Class<?> targetType = determineTargetType(beanName, mbdToUse, null);

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

        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    private Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... targetType) {
        return null;
    }


}