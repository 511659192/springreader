// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

import javax.annotation.Nullable;
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

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditorCache = Maps.newHashMap();

    @Nullable
    private Map<String, CustomEditorHolder> customEditorsForPath = Maps.newHashMap();

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        this.customEditors.put(requiredType, propertyEditor);
    }

    @Override
    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        if (StringUtils.isEmpty(propertyPath)) {
            return getCustomEditor(requiredType);
        }

        if (MapUtils.isEmpty(customEditorsForPath)) {
            return null;
        }

        PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
        if (editor != null) {
            return editor;
        }

        return null;
    }

    private PropertyEditor getCustomEditor(String propertyPath, Class<?> requiredType) {
        CustomEditorHolder holder = this.customEditorsForPath.get(propertyPath);
        if (holder == null) {
            return null;
        }
        return holder.getPropertyEditor(requiredType);
    }

    private PropertyEditor getCustomEditor(Class<?> requiredType) {
        PropertyEditor propertyEditor = this.customEditors.get(requiredType);
        if (propertyEditor != null) {
            return propertyEditor;
        }

        propertyEditor = customEditorCache.get(requiredType);
        if (propertyEditor != null) {
            return propertyEditor;
        }

        for (Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
            Class<?> key = entry.getKey();
            if (key.isAssignableFrom(requiredType)) {
                this.customEditorCache.put(requiredType, entry.getValue());
                return entry.getValue();
            }
        }


        return null;
    }

    private static final class CustomEditorHolder {

        @Getter
        private final PropertyEditor propertyEditor;

        @Getter
        @Nullable
        private final Class<?> registeredType;

        private CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {
            this.propertyEditor = propertyEditor;
            this.registeredType = registeredType;
        }

        public PropertyEditor getPropertyEditor(Class<?> requiredType) {
            if (this.registeredType == null) {
                return this.propertyEditor;
            }

            if (requiredType == null && ClassUtils.isContainer(this.registeredType)) {
                return this.propertyEditor;
            }

            if (ClassUtils.isAssignable(this.registeredType, requiredType)) {
                return this.propertyEditor;
            }

            return null;
        }
    }
}