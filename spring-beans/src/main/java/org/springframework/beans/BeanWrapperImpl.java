// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 3:49 下午
 **/
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper{
    public BeanWrapperImpl(Object beanInstance) {
        super(beanInstance);
    }
}