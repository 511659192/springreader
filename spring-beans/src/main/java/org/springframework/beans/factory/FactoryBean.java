// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 5:08 下午
 **/
public interface FactoryBean {

    @Nullable
    Class<?> getObjectType();

}