// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 3:59 下午
 **/
@Slf4j
public class PostProcessorRegistrationDelegate {
    public static void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        Set<String> processedBeans = new HashSet<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        List<BeanDefinitionRegistryPostProcessor> priorityOrdereds = postProcessBeanDefinitionRegistry(beanFactory, processedBeans, PriorityOrdered.class);
        registryProcessors.addAll(priorityOrdereds);

        List<BeanDefinitionRegistryPostProcessor> ordereds = postProcessBeanDefinitionRegistry(beanFactory, processedBeans, Ordered.class);
        registryProcessors.addAll(ordereds);

        while (true) {
            List<BeanDefinitionRegistryPostProcessor> normals = postProcessBeanDefinitionRegistry(beanFactory, processedBeans, null);
            if (CollectionUtils.isNotEmpty(normals)) {
                registryProcessors.addAll(normals);
            } else {
                break;
            }
        }

        postProcessBeanFactory(registryProcessors, beanFactory);
        postProcessBeanFactory(beanFactory, processedBeans, PriorityOrdered.class);
        postProcessBeanFactory(beanFactory, processedBeans, Ordered.class);
        postProcessBeanFactory(beanFactory, processedBeans, null);
    }

    private static List<BeanFactoryPostProcessor> postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, Set<String> processedBeans, Class<?> requiredType) {
        List<BeanFactoryPostProcessor> registryPostProcessors = Lists.newArrayList();

        String[] registryPostPorcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        for (String postProcessorName : registryPostPorcessorNames) {
            if (processedBeans.contains(postProcessorName)) {
                continue;
            }

            if (requiredType != null && !beanFactory.isTypeMatch(postProcessorName, requiredType)) {
                continue;
            }

            registryPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
            processedBeans.add(postProcessorName);
        }

        postProcessBeanFactory(registryPostProcessors, beanFactory);
        return registryPostProcessors;
    }


    private static List<BeanDefinitionRegistryPostProcessor> postProcessBeanDefinitionRegistry(ConfigurableListableBeanFactory beanFactory, Set<String> processedBeans, Class<?> requiredType) {

        List<BeanDefinitionRegistryPostProcessor> registryPostProcessors = Lists.newArrayList();

        String[] registryPostPorcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String postProcessorName : registryPostPorcessorNames) {
            if (processedBeans.contains(postProcessorName)) {
                continue;
            }

            if (requiredType != null && !beanFactory.isTypeMatch(postProcessorName, requiredType)) {
                continue;
            }

            registryPostProcessors.add(beanFactory.getBean(postProcessorName, BeanDefinitionRegistryPostProcessor.class));
            processedBeans.add(postProcessorName);
        }

        postProcessBeanDefinitionRegistry(registryPostProcessors, (BeanDefinitionRegistry) beanFactory);
        return registryPostProcessors;
    }

    private static void postProcessBeanFactory(List<? extends BeanFactoryPostProcessor> registryProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor registryProcessor : registryProcessors) {
            registryProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private static void postProcessBeanDefinitionRegistry(List<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }

    }
}