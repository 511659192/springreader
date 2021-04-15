// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Comparator;
import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 11:56 上午
 **/
@Slf4j
public class AnnotationAwareOrderComparator extends OrderComparator {

    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

    Ordering ordering = Ordering.natural();

    List<Comparator> comparators = Lists.newArrayList();

    {
        comparators.add((l, r) -> ordering.reverse().compare(l instanceof PriorityOrdered, r instanceof PriorityOrdered));
        comparators.add((l, r) -> ordering.reverse().compare(l instanceof Ordered, r instanceof Ordered));
        comparators.add((l, r) -> ordering.compare(((Ordered) l).getOrder(), ((Ordered) r).getOrder()));
    }


    @Override
    public int compare(Object l, Object r) {
        for (Comparator comparator : this.comparators) {
            int compare = comparator.compare(l, r);
            if (compare != 0) {
                return compare;
            }
        }

        return 1;
    }
}