// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:44 下午
 **/
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourcePatternResolver, EnvironmentCapable {


}