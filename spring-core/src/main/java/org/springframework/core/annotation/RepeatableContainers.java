// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 2:23 下午
 **/
public abstract class RepeatableContainers {

    public static RepeatableContainers standardRepeatables() {
        return new RepeatableContainers() {
        };
    }
}