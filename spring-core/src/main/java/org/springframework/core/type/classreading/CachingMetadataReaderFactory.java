// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceLoader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 11:25 上午
 **/
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

    public static final int DEFAULT_CACHE_LIMIT = 256;

    private Map<Resource, MetadataReader> metadataReaderCache;

    public CachingMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
        this.metadataReaderCache = new LocalResourceCache(DEFAULT_CACHE_LIMIT);
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) {
        MetadataReader metadataReader;

        synchronized (this.metadataReaderCache) {
            metadataReader = this.metadataReaderCache.get(resource);
            if (metadataReader == null) {
                metadataReader = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader);
            }

            return metadataReader;
        }
    }

    private static class LocalResourceCache extends LinkedHashMap<Resource, MetadataReader> {

        private volatile int cacheLimit;

        public LocalResourceCache(int cacheLimit) {
            super(cacheLimit, 0.75f);
            this.cacheLimit = cacheLimit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
            return size() > this.cacheLimit;
        }
    }
}