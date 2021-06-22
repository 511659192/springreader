// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/7 20:57
 **/
public class AopNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("component-scan", new AspectJAutoProxyBeanDefinitionParser());
    }
}