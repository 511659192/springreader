// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:58
 **/
@Slf4j
public abstract class NamespaceHandlerSupport implements NamespaceHandler {

    public NamespaceHandlerSupport() {
        this.init();
    }

    private final Map<String, BeanDefinitionParser> parsers = new HashMap<>();

    protected final void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser) {
        this.parsers.put(elementName, parser);
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String localName = element.getLocalName();
        log.info("localName:{}", localName);
        BeanDefinitionParser parser = getParser(localName);
        return parser.parse(element, parserContext);
    }

    private BeanDefinitionParser getParser(String elementName) {
        return this.parsers.get(elementName);
    }
}