// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:55 下午
 **/
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    protected String getPropertyAsRawString(String text) {
        log.info("##PropertySourcesPropertyResolver.getPropertyAsRawString text:{}", text);
        if (this.propertySources == null) {
            return null;
        }

        for (PropertySource<?> source : this.propertySources) {
            Object value = source.getProperty(text);
            if (value != null) {
                return String.valueOf(value);
            }
        }

        return null;
    }
}