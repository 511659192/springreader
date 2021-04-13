// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:15 下午
 **/
@Slf4j
public abstract class AbstractEnvironment implements ConfigurableEnvironment {


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

    @Override
    public Map<String, Object> getSystemEnvironment() {
        return (Map) System.getenv();
    }


    @Override
    public Map<String, Object> getSystemProperties() {
        return (Map) System.getProperties();
    }
}