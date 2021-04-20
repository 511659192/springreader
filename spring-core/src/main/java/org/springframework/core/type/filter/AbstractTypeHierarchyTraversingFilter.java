// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.filter;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/20 10:29 上午
 **/
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {


    private final boolean considerInherited;
    private final boolean considerInterfaces;


    public AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }
}