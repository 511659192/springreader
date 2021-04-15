// Copyright (C) 2021 Meituan
// All rights reserved
package com.users.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.PriorityOrdered;
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
public class BeanFactoryPostProcessorPriorityOrdered2 implements BeanFactoryPostProcessor, PriorityOrdered, ResourceLoaderAware {


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
    public int getOrder() {
        return 2;
    }
}