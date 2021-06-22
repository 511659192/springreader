// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 3:59 下午
 **/
@Slf4j
public class PostProcessorRegistrationDelegate {
    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        Set<String> processedBeans = new HashSet<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        List<BeanDefinitionRegistryPostProcessor> priorityOrderedProcessors = postProcessBeanDefinitionRegistries(beanFactory, processedBeans, PriorityOrdered.class);
        registryProcessors.addAll(priorityOrderedProcessors);

        List<BeanDefinitionRegistryPostProcessor> orderedProcessors = postProcessBeanDefinitionRegistries(beanFactory, processedBeans, Ordered.class);
        registryProcessors.addAll(orderedProcessors);

        while (true) {
            List<BeanDefinitionRegistryPostProcessor> normals = postProcessBeanDefinitionRegistries(beanFactory, processedBeans, null);
            if (CollectionUtils.isNotEmpty(normals)) {
                registryProcessors.addAll(normals);
            } else {
                break;
            }
        }

        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        // BeanFactoryPostProcessor.class
        postProcessBeanFactories(beanFactory, processedBeans, PriorityOrdered.class);
        postProcessBeanFactories(beanFactory, processedBeans, Ordered.class);
        postProcessBeanFactories(beanFactory, processedBeans, null);
    }

    private static List<BeanFactoryPostProcessor> postProcessBeanFactories(ConfigurableListableBeanFactory beanFactory, Set<String> processedBeans, Class<?> requiredType) {
        List<BeanFactoryPostProcessor> newProcessors = Lists.newArrayList();

        String[] beanFactoryPostProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        for (String postProcessorName : beanFactoryPostProcessorNames) {
            if (processedBeans.contains(postProcessorName)) {
                continue;
            }

            if (requiredType != null && !beanFactory.isTypeMatch(postProcessorName, requiredType)) {
                continue;
            }

            newProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
            processedBeans.add(postProcessorName);
        }

        invokeBeanFactoryPostProcessors(newProcessors, beanFactory);
        return newProcessors;
    }

    private static List<BeanPostProcessor> registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, Set<String> processedBeanNames, Class<?> requiredType) {
        List<BeanPostProcessor> newProcessors = Lists.newArrayList();

        String[] beanFactoryPostProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        for (String postProcessorName : beanFactoryPostProcessorNames) {
            if (processedBeanNames.contains(postProcessorName)) {
                continue;
            }

            if (requiredType != null && !beanFactory.isTypeMatch(postProcessorName, requiredType)) {
                continue;
            }

            newProcessors.add(beanFactory.getBean(postProcessorName, BeanPostProcessor.class));
            processedBeanNames.add(postProcessorName);
        }

        if (CollectionUtils.isEmpty(newProcessors)) {
            return Collections.emptyList();
        }

        sortPostProcessors(newProcessors, beanFactory);
        doAddBeanPostProcessors(beanFactory, newProcessors);
        return newProcessors;
    }

    private static void doAddBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> newProcessors) {
        for (BeanPostProcessor beanPostProcessor : newProcessors) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    private static void sortPostProcessors(List<?> processors, ConfigurableListableBeanFactory beanFactory) {
        if (CollectionUtils.size(processors) <= 1) {
            return;
        }

        Comparator<Object> dependencyComparator = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        processors.sort(dependencyComparator);
    }


    private static List<BeanDefinitionRegistryPostProcessor> postProcessBeanDefinitionRegistries(ConfigurableListableBeanFactory beanFactory, Set<String> processedBeans, Class<?> requiredType) {

        List<BeanDefinitionRegistryPostProcessor> newProcessors = Lists.newArrayList();

        String[] beanDefinitionRegistryPostProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);

        for (String postProcessorName : beanDefinitionRegistryPostProcessorNames) {
            if (processedBeans.contains(postProcessorName)) {
                continue;
            }

            if (requiredType != null && !beanFactory.isTypeMatch(postProcessorName, requiredType)) {
                continue;
            }

            newProcessors.add(beanFactory.getBean(postProcessorName, BeanDefinitionRegistryPostProcessor.class));
            processedBeans.add(postProcessorName);
        }

        sortPostProcessors(newProcessors, beanFactory);
        invokeBeanDefinitionRegistryPostProcessors(newProcessors, (BeanDefinitionRegistry) beanFactory);
        return newProcessors;
    }

    private static void invokeBeanFactoryPostProcessors(List<? extends BeanFactoryPostProcessor> registryProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor registryProcessor : registryProcessors) {
            registryProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(List<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }

    }

    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        String[] beanPostProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

        int count = beanFactory.getBeanPostProcessorCount() + 1 + CollectionUtils.size(beanPostProcessorNames);
        beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, count));

        Set<String> processedBeans = Sets.newHashSet();
        registerBeanPostProcessors(beanFactory, processedBeans, PriorityOrdered.class);
        registerBeanPostProcessors(beanFactory, processedBeans, Ordered.class);
        registerBeanPostProcessors(beanFactory, processedBeans, ((Class) null));

        processedBeans.clear();
        registerBeanPostProcessors(beanFactory, processedBeans, MergedBeanDefinitionPostProcessor.class);
    }
}