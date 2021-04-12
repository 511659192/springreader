// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:18 下午
 **/
public class StandardEnvironment extends AbstractEnvironment {

    @Override
    void customizePropertySources(MutablePropertySources mutablePropertySources) {
        log.info("加载自定义属性源");
        PropertiesPropertySource systemProperties = new PropertiesPropertySource("systemProperties", System.getProperties());
        mutablePropertySources.addLast(systemProperties);

        SystemEnvironmentPropertySource systemEnvironment = new SystemEnvironmentPropertySource("systemEnvironment", System.getenv());
        mutablePropertySources.addLast(systemEnvironment);

    }
}