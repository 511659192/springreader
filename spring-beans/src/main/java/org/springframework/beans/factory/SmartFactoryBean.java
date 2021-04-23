// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/23 2:18 下午
 **/
public interface SmartFactoryBean<T> extends FactoryBean<T> {

    default boolean isEagerInit() {
        return false;
    }
}