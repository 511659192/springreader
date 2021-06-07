// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.framework;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.Ordered;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/7 21:39
 **/
public class ProxyProcessorSupport extends ProxyConfig implements Ordered, BeanClassLoaderAware, AopInfrastructureBean {

    private int order = Ordered.LOWEST_PRECEDENCE;

    @Override
    public int getOrder() {
        return order;
    }
}