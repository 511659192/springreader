// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import lombok.Setter;
import org.springframework.core.convert.ConversionService;

import java.beans.PropertyEditor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 4:00 下午
 **/
public class PropertyEditorRegistrySupport implements PropertyEditorRegistry {

    private boolean defaultEditorsActive;

    private Map<Class<?>, PropertyEditor> customEditors = new LinkedHashMap<>(16);

    @Setter
    private ConversionService conversionService;

    private boolean configValueEditorsActive;

    protected void registerDefaultEditors() {
        this.defaultEditorsActive = true;
    }

    public void useConfigValueEditors() {
        this.configValueEditorsActive = true;
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        this.customEditors.put(requiredType, propertyEditor);
    }
}