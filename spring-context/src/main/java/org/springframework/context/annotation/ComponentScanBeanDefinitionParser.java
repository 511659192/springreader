// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.annotation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

import java.util.Set;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/30 0:01
 **/
@Slf4j
public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        log.info("tagName:{}", element.getTagName());

        String attribute = element.getAttribute("base-package");
        String[] basePackages = StringUtils.split(attribute, ",");
        ClassPathBeanDefinitionScanner scanner = configScanner(element, parserContext);
        Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);
        return null;
    }

    private void registerComponents(XmlReaderContext readerContext, Set<BeanDefinitionHolder> beanDefinitions, Element element) {
        log.info("beanDefinitions:{}", beanDefinitions.size());
        Object source = readerContext.extractSource(element);
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), source);
        for (BeanDefinitionHolder beanDefinition : beanDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(beanDefinition));
        }

        Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(readerContext.getRegistry(), source);
        for (BeanDefinitionHolder processorDefinition : processorDefinitions) {
            compositeDef.addNestedComponent(new BeanComponentDefinition(processorDefinition));
        }

        readerContext.fireComponentRegistered(compositeDef);
    }

    ClassPathBeanDefinitionScanner configScanner(Element element, ParserContext parserContext) {
        ClassPathBeanDefinitionScanner scanner = createScanner(parserContext);
        return scanner;
    }

    private ClassPathBeanDefinitionScanner createScanner(ParserContext parserContext) {
        log.info("");
        XmlReaderContext readerContext = parserContext.getReaderContext();
        BeanDefinitionRegistry registry = readerContext.getRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, readerContext.getEnvironment(), readerContext.getResourceLoader());
        return scanner;
    }

}