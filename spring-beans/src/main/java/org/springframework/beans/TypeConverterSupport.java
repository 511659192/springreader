// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 4:00 下午
 **/
public class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

    @Nullable
    TypeConverterDelegate typeConverterDelegate;

    @Override
    public Object convertIfNecessary(Object bean, Class<?> requiredType) {
        return null;
    }
}