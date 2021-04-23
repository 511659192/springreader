// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/23 4:55 下午
 **/
@FunctionalInterface
public interface ExceptionCatcher {

    RuntimeException unchecked(Exception e);
}