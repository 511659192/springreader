// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Objects;


/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 7:29 下午
 **/
@Slf4j
public class BeanDefinitionParserDelegate {

    public static final String AUTOWIRE_NO_VALUE = "no";
    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";

    final XmlReaderContext readerContext;

    public BeanDefinitionParserDelegate(XmlReaderContext readerContext) {
        this.readerContext = readerContext;
    }

    public boolean isDefaultNamespace(Node node) {
        String namespaceURI = node.getNamespaceURI();
        return Objects.equals(namespaceURI, "http://www.springframework.org/schema/beans");
    }

    public BeanDefinition parseCustomElement(Element element) {
        String namespaceURI = element.getNamespaceURI();
//        log.info("namespaceURI:{}", namespaceURI);
        if (StringUtils.isBlank(namespaceURI)) {
            return null;
        }

        NamespaceHandlerResolver namespaceHandlerResolver = this.readerContext.getNamespaceHandlerResolver();
        NamespaceHandler namespaceHandler = namespaceHandlerResolver.resolve(namespaceURI);
        ParserContext parserContext = new ParserContext(this.readerContext, this);
        return namespaceHandler.parse(element, parserContext);
    }

    public boolean nodeNameEquals(Node node, String name) {
        return Objects.equals(node.getNodeName(), name) || Objects.equals(node.getLocalName(), name);
    }

    public BeanDefinitionHolder parseBeanDefinitionElement(Element element) {
        String beanName = element.getAttribute("id");
        AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(element, beanName);
        return new BeanDefinitionHolder(beanDefinition, beanName);
    }

    private AbstractBeanDefinition parseBeanDefinitionElement(Element element, String beanName) {
        String className = element.getAttribute("class");
        AbstractBeanDefinition beanDefinition = createBeanDefinition(className, beanName);
        parseBeanDefinitionAttributes(element, beanName, beanDefinition);
        parseMetaElements(element, beanDefinition);
        parseConstructorArgElements(element, beanDefinition);
        parsePropertyElements(element, beanDefinition);
        return beanDefinition;
    }

    private void parsePropertyElements(Element element, AbstractBeanDefinition beanDefinition) {

    }

    private void parseConstructorArgElements(Element element, AbstractBeanDefinition beanDefinition) {


    }

    private void parseMetaElements(Element element, AbstractBeanDefinition beanDefinition) {
        // not used
    }

    private AbstractBeanDefinition parseBeanDefinitionAttributes(Element element, String beanName, AbstractBeanDefinition beanDefinition) {
        beanDefinition.setScope(element.getAttribute("scope"));


        String autowire = element.getAttribute("autowire");
        beanDefinition.setAutowireMode(getAutowireMode(autowire));
        beanDefinition.setInitMethodName(element.getAttribute("init-method"));
        return beanDefinition;
    }

    public int getAutowireMode(String attrValue) {
        String attr = attrValue;
        if (isDefaultValue(attr)) {
            attr = AUTOWIRE_BY_TYPE_VALUE;
        }
        int autowire = AbstractBeanDefinition.AUTOWIRE_NO;
        if (AUTOWIRE_BY_NAME_VALUE.equals(attr)) {
            autowire = AbstractBeanDefinition.AUTOWIRE_BY_NAME;
        }
        else if (AUTOWIRE_BY_TYPE_VALUE.equals(attr)) {
            autowire = AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
        }
        else if (AUTOWIRE_CONSTRUCTOR_VALUE.equals(attr)) {
            autowire = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
        }
        // Else leave default value.
        return autowire;
    }

    private boolean isDefaultValue(String value) {
        return StringUtils.isNotBlank(value) || "".equals(value);
    }

    private AbstractBeanDefinition createBeanDefinition(String className, String beanName) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(className);
        beanDefinition.setBeanName(beanName);
        return beanDefinition;
    }

    public void initDefault(Element root) {
    }



    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        BeanDefinitionDefaults bdd = new BeanDefinitionDefaults();
        //        bdd.setLazyInit(TRUE_VALUE.equalsIgnoreCase(this.defaults.getLazyInit()));
        //        bdd.setAutowireMode(getAutowireMode(DEFAULT_VALUE));
        //        bdd.setInitMethodName(this.defaults.getInitMethod());
        //        bdd.setDestroyMethodName(this.defaults.getDestroyMethod());
        return bdd;
    }

    public String[] getAutowireCandidatePatterns() {
        //        String candidatePattern = this.defaults.getAutowireCandidates();
        //        return (candidatePattern != null ? StringUtils.commaDelimitedListToStringArray(candidatePattern) : null);
        return null;
    }
}