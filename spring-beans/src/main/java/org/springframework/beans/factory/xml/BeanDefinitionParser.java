// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:59
 **/
public interface BeanDefinitionParser {
    BeanDefinition parse(Element element, ParserContext parserContext);
}