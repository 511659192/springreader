// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:21 下午
 **/
public class MutablePropertySources implements PropertySources {

    private final List<PropertySource<?>> propertySourceList = new ArrayList<>();

    public void addLast(PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            this.propertySourceList.remove(propertySource);
            this.propertySourceList.add(propertySource);
        }
    }

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }
}