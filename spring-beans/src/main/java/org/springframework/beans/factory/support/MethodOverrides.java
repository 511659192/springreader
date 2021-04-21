// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/21 10:57 上午
 **/
public class MethodOverrides {

    private final Set<MethodOverride> overrides = new CopyOnWriteArraySet<>();

    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }
}