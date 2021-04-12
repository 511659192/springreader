// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:50 下午
 **/
public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {

    private Object wrappedObject;

    public AbstractNestablePropertyAccessor(Object beanInstance) {
        this.wrappedObject = beanInstance;
        registerDefaultEditors();
    }

    public final Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }
}