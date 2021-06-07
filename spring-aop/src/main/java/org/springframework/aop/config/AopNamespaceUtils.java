// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/6/7 21:22
 **/
public class AopNamespaceUtils {
    public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(ParserContext parserContext, Element element) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        Object source = parserContext.extractSource(element);
        BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry, source);
    }
}