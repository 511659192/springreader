// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceLoader;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:21 下午
 **/
@RequiredArgsConstructor
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {

    @NonNull
    ResourceLoader resourceLoader;

    @Override
    public MetadataReader getMetadataReader(Resource resource) {
        return new SimpleMetadataReader(resource, resourceLoader.getClassLoader());
    }
}