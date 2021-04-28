// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import org.springframework.core.MethodParameter;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/25 10:36 上午
 **/
public class InjectionPoint {
    protected final Field field;


    @Nullable
    protected MethodParameter methodParameter;


    @Nullable
    private volatile Annotation[] fieldAnnotations;

    public InjectionPoint(Field field) {

        this.field = field;
    }
}