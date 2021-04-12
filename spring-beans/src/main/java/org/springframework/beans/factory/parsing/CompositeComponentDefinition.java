// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.parsing;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 7:06 下午
 **/
@Slf4j
public class CompositeComponentDefinition extends AbstractComponentDefinition {

    private String name;
    private Object source;

    private final List<ComponentDefinition> nestedComponents = new ArrayList<>();

    public CompositeComponentDefinition(String name, Object source) {
        this.name = name;
        this.source = source;
        log.info("name:{] source:{}", name, source);
    }

    public void addNestedComponent(ComponentDefinition componentDefinition) {
        this.nestedComponents.add(componentDefinition);
    }
}