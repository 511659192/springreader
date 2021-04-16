// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import org.springframework.core.convert.TypeDescriptor;

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
    public <T> T convertIfNecessary(Object bean, Class<T> requiredType) {
        return convertIfNecessary(bean, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    private <T> T convertIfNecessary(Object bean, Class<T> requiredType, TypeDescriptor typeDescriptor) {
        T t = this.typeConverterDelegate.convertIfNecessary(null, null, bean, requiredType, typeDescriptor);
        return t;
    }
}