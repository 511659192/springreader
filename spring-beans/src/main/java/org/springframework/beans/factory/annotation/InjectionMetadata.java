// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.annotation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.PropertyValues;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/23 3:38 下午
 **/
public class InjectionMetadata {

    private final Class<?> targetClass;
    private final Collection<InjectedElement> injectedElements;
    @Nullable
    private volatile Set<InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> injectedElements) {
        this.targetClass = targetClass;
        this.injectedElements = injectedElements;
    }

    public static InjectionMetadata forElements(List<InjectedElement> elements, Class<?> beanClass) {
        return new InjectionMetadata(beanClass, elements);
    }

    public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate = (checkedElements != null ? checkedElements : this.injectedElements);
        if (CollectionUtils.isEmpty(elementsToIterate)) {
            return;
        }

        for (InjectedElement element : elementsToIterate) {
            element.inject(target, beanName, pvs);
        }
    }

    public static final InjectionMetadata EMPTY = new InjectionMetadata(Object.class, Collections.emptyList()) {
        @Override
        public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {
        }
    };

    public abstract static class InjectedElement {

        protected final Member member;

        protected final boolean isField;

        @Nullable
        protected final PropertyDescriptor pd;

        @Nullable
        protected volatile Boolean skip;

        protected InjectedElement(Member member, @Nullable PropertyDescriptor pd) {
            this.member = member;
            this.isField = (member instanceof Field);
            this.pd = pd;
        }

        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) {
            if (this.isField) {
                try {
                    Field field = (Field) this.member;
                    field.setAccessible(true);
                    field.set(target, getResourceToInject(target, requestingBeanName));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (checkPropertySkipping(pvs)) {
                return;
            }
            try {
                Method method = (Method) this.member;
                method.setAccessible(true);
                method.invoke(target, getResourceToInject(target, requestingBeanName));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private boolean checkPropertySkipping(PropertyValues pvs) {
            return false;
        }

        @Nullable
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return null;
        }
    }

}