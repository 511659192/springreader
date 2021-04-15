// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:23 上午
 **/
@Slf4j
@Component
public class ConfigurationClassPostProcessorOrdered2 implements BeanDefinitionRegistryPostProcessor, Ordered, ResourceLoaderAware {


    ResourceLoader resourceLoader;
    private MetadataReaderFactory metadataReaderFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        log.info("");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        log.info("");

    }

    @Override
    public int getOrder() {
        return 2;
    }
}