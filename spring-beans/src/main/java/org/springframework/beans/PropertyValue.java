// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 11:14 上午
 **/
public class PropertyValue {
    private Object value;

    @Nullable
    public Object getValue() {
        return this.value;
    }

}