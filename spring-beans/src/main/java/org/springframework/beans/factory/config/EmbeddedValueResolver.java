// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.util.StringValueResolver;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 3:00 下午
 **/
public class EmbeddedValueResolver implements StringValueResolver {
    public EmbeddedValueResolver(ConfigurableListableBeanFactory beanFactory) {

    }

    @Nullable
    @Override
    public String resolveStringValue(String strVal) {
        return null;
    }
}