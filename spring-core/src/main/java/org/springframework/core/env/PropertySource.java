// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:38 下午
 **/
public abstract class PropertySource<T> {

    final String name;

    final T source;

    public PropertySource(String name, T source) {
        this.name = name;
        this.source = source;
    }

    public abstract Object getProperty(String name);

}