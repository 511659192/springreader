// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:08 下午
 **/
public interface ListableBeanFactory extends BeanFactory {

    int getBeanDefinitionCount();

    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletions, boolean allowEagerInit);
}