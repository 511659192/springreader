// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:59 上午
 **/
public interface Ordered {

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}