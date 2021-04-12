// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import org.springframework.core.convert.ConversionService;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:51 下午
 **/
public interface ConfigurablePropertyAccessor extends PropertyEditorRegistry, TypeConverter, PropertyAccessor {

    void setConversionService(ConversionService conversionService);
}