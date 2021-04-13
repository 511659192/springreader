// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 8:11 下午
 **/
public class TypeConverterDelegate {
    private PropertyEditorRegistrySupport propertyEditorRegistrySupport;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistrySupport) {
        this.propertyEditorRegistrySupport = propertyEditorRegistrySupport;
    }
}