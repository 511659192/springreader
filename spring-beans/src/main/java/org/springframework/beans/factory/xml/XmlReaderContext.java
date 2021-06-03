// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.ResourceLoaderCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceLoader;
import org.w3c.dom.Element;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 4:15 下午
 **/
@Getter
@Setter
@Slf4j
public class XmlReaderContext implements EnvironmentCapable, ResourceLoaderCapable {

    @Getter
    private XmlBeanDefinitionReader beanDefinitionReader;

    @Getter
    private NamespaceHandlerResolver namespaceHandlerResolver;

    private final Resource resource;


    private final SourceExtractor sourceExtractor;


    public BeanDefinitionRegistry getRegistry() {
        return this.beanDefinitionReader.getRegistry();
    }

    public XmlReaderContext(Resource resource, SourceExtractor sourceExtractor, XmlBeanDefinitionReader beanDefinitionReader, NamespaceHandlerResolver namespaceHandlerResolver) {
        this.resource = resource;
        this.sourceExtractor = sourceExtractor;
        this.beanDefinitionReader = beanDefinitionReader;
        this.namespaceHandlerResolver = namespaceHandlerResolver;
    }

    @Override
    public Environment getEnvironment() {
        return this.beanDefinitionReader.getEnvironment();
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return beanDefinitionReader.getResourceLoader();
    }

    public Object extractSource(Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }

    public void fireComponentRegistered(CompositeComponentDefinition compositeDef) {
    }
}

