// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:30 下午
 **/
public interface HierarchicalBeanFactory extends BeanFactory {

    boolean containsLocalBean(String name);
    @Nullable
    BeanFactory getParentBeanFactory();
}