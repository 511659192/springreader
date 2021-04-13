// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 8:09 下午
 **/
public class SimpleTypeConverter extends TypeConverterSupport{

    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
    }
}