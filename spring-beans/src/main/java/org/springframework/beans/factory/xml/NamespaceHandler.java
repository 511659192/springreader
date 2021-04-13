// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/26 5:12 下午
 **/
public interface NamespaceHandler {

    BeanDefinition parse(Element element, ParserContext parserContext);

    void init();
}