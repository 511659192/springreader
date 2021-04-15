// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.target;

import org.springframework.aop.TargetSource;

import java.io.Serializable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 4:25 下午
 **/
public class SingletonTargetSource implements TargetSource, Serializable {
    private final Object target;

    public SingletonTargetSource(Object target) {
        this.target = target;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override
    public Object getTarget() {
        return this.target;
    }
}