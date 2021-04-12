// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/8 10:28 上午
 **/
public class BeanMetadataAttributeAccessor {

    @Getter
    @Setter
    @Nullable
    private Object source;
}