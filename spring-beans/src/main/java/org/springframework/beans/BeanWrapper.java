// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:24 下午
 **/
public interface BeanWrapper extends ConfigurablePropertyAccessor {

    Object getWrappedInstance();

    Class<?> getWrappedClass();
}