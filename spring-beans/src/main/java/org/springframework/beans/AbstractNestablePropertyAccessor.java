// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:50 下午
 **/
public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {

    @Getter
    @Setter
    Object wrappedObject;

    @Nullable
    Object rootOjbect;


    public AbstractNestablePropertyAccessor(Object beanInstance) {
        this.wrappedObject = beanInstance;
        registerDefaultEditors();
    }

    public AbstractNestablePropertyAccessor(boolean registerDefaultEditors) {
        if (registerDefaultEditors) {
            registerDefaultEditors();
        }
    }

    public final Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }

}