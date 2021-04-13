// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringValueResolver;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 2:54 下午
 **/
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private ConfigurableApplicationContext applicationContext;
    private final StringValueResolver embeddedValueResolver;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());

    }
}