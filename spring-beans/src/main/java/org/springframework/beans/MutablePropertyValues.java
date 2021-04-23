// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:09 上午
 **/
public class MutablePropertyValues implements PropertyValues, Serializable {


    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    @Nullable
    private Set<String> processedProperties;

    public MutablePropertyValues() {
    }

    public MutablePropertyValues(MutablePropertyValues pvs) {

    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return new PropertyValue[0];
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(propertyValueList);
    }


}