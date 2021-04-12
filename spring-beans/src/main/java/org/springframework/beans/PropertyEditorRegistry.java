// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import java.beans.PropertyEditor;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 4:01 下午
 **/
public interface PropertyEditorRegistry {

    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);
}