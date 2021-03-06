// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.MutablePropertyValues;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 5:38 下午
 **/
public interface BeanDefinition {
    int ROLE_INFRASTRUCTURE = 2;

    void setScope(String scope);

    /**
     * Return the name of the current target scope for this bean,
     * or {@code null} if not known yet.
     */
    String getScope();

    String getBeanClassName();

    MutablePropertyValues getPropertyValues();

    void setRole(int role);

    Class<?> getBeanClass();

    boolean isAbstract();

    boolean isSingleton();
    boolean isLazyInit();

    boolean isAutowireCandidate();

    boolean isPrimary();


    @Nullable
    default String getParentName() {
        return null;
    }

    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    default ConstructorArgumentValues getConstructorArgumentValues() {
        return null;
    }

    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

}