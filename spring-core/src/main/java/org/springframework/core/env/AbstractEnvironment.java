// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:15 下午
 **/
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    Logger log = LoggerFactory.getLogger(getClass());

    private final MutablePropertySources propertySources = new MutablePropertySources();

    private final ConfigurablePropertyResolver propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);


    public AbstractEnvironment() {
        log.info("");
        this.customizePropertySources(this.propertySources);
    }

    void customizePropertySources(MutablePropertySources mutablePropertySources) {
    }


    @Override
    public String resolveRequiredPlaceholders(String text) {
        log.info("text:{}", text);
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }
}