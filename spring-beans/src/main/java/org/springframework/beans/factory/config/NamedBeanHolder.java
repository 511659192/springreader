// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import org.springframework.beans.factory.NamedBean;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/16 10:51 上午
 **/
public class NamedBeanHolder<T> implements NamedBean {

    @Getter
    private T beanInstance;

    @Getter
    private String beanName;

    public NamedBeanHolder(String beanName, T beanInstance) {
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }
}