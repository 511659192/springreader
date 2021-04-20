// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 23:05
 **/
abstract class AbstractMergedAnnotation<T extends Annotation> implements MergedAnnotation<T> {


    @Override
    public boolean isMetaPresent() {
        return isPresent() && getDistance() > 0;
    }
}