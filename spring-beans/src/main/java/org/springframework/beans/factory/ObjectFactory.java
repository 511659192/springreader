// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 5:17 下午
 **/
@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject();
}