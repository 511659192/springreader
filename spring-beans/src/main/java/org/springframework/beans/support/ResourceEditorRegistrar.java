// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.support;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourceLoader;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 2:26 下午
 **/
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {
    private final ResourceLoader resourceLoader;
    private final Environment environment;

    public ResourceEditorRegistrar(ResourceLoader resourceLoader, Environment environment) {

        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {

    }
}