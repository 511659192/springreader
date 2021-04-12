// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import java.io.Serializable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:09 上午
 **/
public class MutablePropertyValues implements PropertyValues, Serializable {
    @Override
    public PropertyValue[] getPropertyValues() {
        return new PropertyValue[0];
    }
}