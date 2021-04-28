// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/25 10:36 上午
 **/
public class DependencyDescriptor extends InjectionPoint {

    private final Class<?> declaringClass;
    private final String fieldName;
    private final boolean required;

    @Getter
    private final boolean eager;
    private Class<?> containingClass;


    @Nullable
    private transient volatile ResolvableType resolvableType;

    @Nullable
    private transient volatile TypeDescriptor typeDescriptor;

    public DependencyDescriptor(Field field, boolean required) {
        this(field, required, true);
    }

    public DependencyDescriptor(Field field, boolean required, boolean eager) {
        super(field);

        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter = this.methodParameter.withContainingClass(containingClass);
        }
        
    }

    public Class<?> getDependencyType() {
        return this.field.getType();
    }
}