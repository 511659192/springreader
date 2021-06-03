// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 7:32 下午
 **/
@Slf4j
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

    private BeanDefinitionParserDelegate delegate;

    private XmlReaderContext readerContext;

    private BeanDefinitionRegistry registry;

    public DefaultBeanDefinitionDocumentReader(XmlReaderContext readerContext) {
        this.readerContext = readerContext;
        this.registry = readerContext.getRegistry();
    }

    @Override
    public void registerBeanDefinitions(Document document) {
        doRegisterBeanDefinitions(document.getDocumentElement());
    }

    private void doRegisterBeanDefinitions(Element root) {
        BeanDefinitionParserDelegate parent = this.delegate;
        this.delegate = createDelegate(this.readerContext, root);

        acceptsProfiles(root.getAttribute("profile"));

        preProcessXml(root);
        parseBeanDefinitions(root, this.delegate);
        postProcessXml(root);

        this.delegate = parent;
    }

    private void acceptsProfiles(String profile) {

    }

    private BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root) {
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        delegate.initDefault(root);
        return delegate;
    }

    private void postProcessXml(Element root) {
    }

    private void preProcessXml(Element root) {
    }

    private void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
//        log.info("root uri:{}", root.getNamespaceURI());
        if (delegate.isDefaultNamespace(root)) {
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (delegate.isDefaultNamespace(element)) {
                        parseDefaultElement(element, delegate);
                    } else {
                        delegate.parseCustomElement(element);
                    }
                }
            }
        } else {
            delegate.parseCustomElement(root);
        }
    }

    private void parseDefaultElement(Element element, BeanDefinitionParserDelegate delegate) {
        if (delegate.nodeNameEquals(element, "bean")) {
            processBeanDefinition(element, delegate);
        }
    }

    private void processBeanDefinition(Element element, BeanDefinitionParserDelegate delegate) {
        BeanDefinitionHolder beanDefinitionHolder = delegate.parseBeanDefinitionElement(element);
        registerBeanDefinition(beanDefinitionHolder, this.registry);
    }
}