// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 11:05 上午
 **/
public interface InitializingBean {

    void afterPropertiesSet();
}