// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import org.springframework.core.OrderComparator;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 11:56 上午
 **/
public class AnnotationAwareOrderComparator extends OrderComparator {

    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();



}