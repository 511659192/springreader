// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 2:56 下午
 **/
@FunctionalInterface
public interface StringValueResolver {
    @Nullable
    String resolveStringValue(String strVal);


}
