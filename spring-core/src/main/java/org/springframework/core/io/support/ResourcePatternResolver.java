// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io.support;

import org.springframework.core.io.Resource;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 5:46 下午
 **/
public interface ResourcePatternResolver extends ResourceLoader {
    Resource[] getResources(String locationPattern);
}