// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import org.springframework.core.convert.TypeDescriptor;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 8:11 下午
 **/
public class TypeConverterDelegate {
    private Object targetObject;
    private PropertyEditorRegistrySupport propertyEditorRegistry;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object beanInstance) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = beanInstance;
    }


    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
                                    @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) {


        this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);


        return null;

    }
}