// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:08 下午
 **/
public interface ListableBeanFactory extends BeanFactory {

    int getBeanDefinitionCount();

    String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletions, boolean allowEagerInit);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);
}