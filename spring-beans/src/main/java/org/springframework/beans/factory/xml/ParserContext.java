// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.Getter;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/26 5:15 下午
 **/
public final class ParserContext {

    @Getter
    private XmlReaderContext readerContext;

    private BeanDefinitionParserDelegate delegate;

    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
        this.readerContext = readerContext;
        this.delegate = delegate;
    }
}