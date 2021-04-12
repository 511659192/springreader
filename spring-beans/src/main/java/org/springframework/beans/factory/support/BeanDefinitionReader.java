// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.core.io.Resource;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 4:06 下午
 **/
public interface BeanDefinitionReader {

    int loadBeanDefinitions(Resource resource);

    BeanDefinitionRegistry getRegistry();

}