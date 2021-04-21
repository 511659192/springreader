// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 10:53 上午
 **/
public class LookupOverride extends MethodOverride {

    private Method method;
    private String beanName;

    public LookupOverride(Method method, String beanName) {

        this.method = method;
        this.beanName = beanName;
    }
}