// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core;

import com.google.common.collect.ComparisonChain;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 2:05 下午
 **/
public class OrderComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        return ComparisonChain.start().result();
    }

    @Nullable
    public Integer getPriority(Object obj) {
        return null;
    }
}