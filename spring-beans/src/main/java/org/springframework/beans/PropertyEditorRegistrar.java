// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import org.springframework.beans.PropertyEditorRegistry;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 4:27 下午
 **/
public interface PropertyEditorRegistrar {

    void registerCustomEditors(PropertyEditorRegistry registry);
}