// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:57
 **/
public class ContextNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("aspectj-autoproxy", new ComponentScanBeanDefinitionParser());
    }
}