// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:16
 **/
@FunctionalInterface
public interface ValueExtractor {
    @Nullable
    Object extract(Method attribute, @Nullable Object object);
}