// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 7:31 下午
 **/
public interface BeanDefinitionDocumentReader {
    void registerBeanDefinitions(Document document);
}