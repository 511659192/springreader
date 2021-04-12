// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 7:07 下午
 **/
public interface BeanMetadataElement {
    @Nullable
    default Object getSource() {
        return null;
    }
}