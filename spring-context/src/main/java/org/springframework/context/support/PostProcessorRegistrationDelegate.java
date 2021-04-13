// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 3:59 下午
 **/
@Slf4j
public class PostProcessorRegistrationDelegate {
    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory,
                                                       List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);


        log.info("postProcessorNames:{}", JsonUtils.toJson(postProcessorNames));
        for (String postProcessorName : postProcessorNames) {
            if (beanFactory.isTypeMatch(postProcessorName, PriorityOrdered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(postProcessorName, BeanDefinitionRegistryPostProcessor.class));


            }
        }
    }
}