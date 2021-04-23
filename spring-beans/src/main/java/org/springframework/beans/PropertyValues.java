// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 11:15 上午
 **/
public interface PropertyValues extends Iterable<PropertyValue> {
    @Override
    default Iterator<PropertyValue> iterator() {
        return Arrays.stream(getPropertyValues()).iterator();
    }

    @Override
    default Spliterator<PropertyValue> spliterator() {
        return Arrays.stream(getPropertyValues()).spliterator();
    }

    PropertyValue[] getPropertyValues();


    boolean isEmpty();
}