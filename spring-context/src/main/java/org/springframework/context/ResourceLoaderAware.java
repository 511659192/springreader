// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context;

import org.springframework.core.io.support.ResourceLoader;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 2:50 下午
 **/
public interface ResourceLoaderAware {

    void setResourceLoader(ResourceLoader resourceLoader);
}