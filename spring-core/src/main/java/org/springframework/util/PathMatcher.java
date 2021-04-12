// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 7:06 下午
 **/
public interface PathMatcher {

    boolean isPattern(String substring);

    boolean match(String path, String pattern);
}