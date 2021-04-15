// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 4:06 下午
 **/
@Slf4j
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    // todo
    private final ThreadLocal<Set<Resource>> resourcesCurrentlyBeingLoaded = ThreadLocal.withInitial(() -> new HashSet<>(4));


    private SourceExtractor sourceExtractor = new NullSourceExtractor();

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        log.info("");
    }

    @Override
    public int loadBeanDefinitions(Resource resource) {

        Set<Resource> currentResources = this.resourcesCurrentlyBeingLoaded.get();

        if (!currentResources.add(resource)) {
            throw new RuntimeException("multi op");
        }

        try {
            InputSource domInputSource = new InputSource(resource.getInputStream());
            Document document = doLoadDocument(domInputSource);
            int count = registerBeanDefinitions(document, resource);
            return count;
        } finally {
            currentResources.remove(resource);
            if (CollectionUtils.isEmpty(currentResources)) {
                resourcesCurrentlyBeingLoaded.remove();
            }
        }
    }

    protected Document doLoadDocument(InputSource inputSource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int registerBeanDefinitions(Document document, Resource resource) {
        log.info("");
        int before = this.getRegistry().getBeanDefinitionCount();
        XmlReaderContext readerContext = createXmlReaderContext(resource);
        BeanDefinitionDocumentReader documentReader = new DefaultBeanDefinitionDocumentReader(readerContext);
        documentReader.registerBeanDefinitions(document);
        return this.getRegistry().getBeanDefinitionCount() - before;
    }

    private XmlReaderContext createXmlReaderContext(Resource resource) {
        ClassLoader classLoader = getResourceLoader().getClassLoader();
        DefaultNamespaceHandlerResolver namespaceHandlerResolver = new DefaultNamespaceHandlerResolver(classLoader);
        XmlReaderContext readerContext = new XmlReaderContext(resource,  this.sourceExtractor, this, namespaceHandlerResolver);
        return readerContext;
    }

}