// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/26 5:12 下午
 **/
@FunctionalInterface
public interface NamespaceHandlerResolver {

    NamespaceHandler resolve(String namespaceUri);
}