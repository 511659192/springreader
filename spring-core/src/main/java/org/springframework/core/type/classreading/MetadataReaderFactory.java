// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:07 下午
 **/
public interface MetadataReaderFactory {
    MetadataReader getMetadataReader(Resource resource);
}