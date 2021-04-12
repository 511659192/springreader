// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type;

import org.springframework.core.annotation.MergedAnnotations;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 3:47 下午
 **/
public interface MethodMetadata extends AnnotatedTypeMetadata {



    class SimpleMethodMetadata implements MethodMetadata {

        @Override
        public MergedAnnotations getAnnotations() {
            return null;
        }
    }
}