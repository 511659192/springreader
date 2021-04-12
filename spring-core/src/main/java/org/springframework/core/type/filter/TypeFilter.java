// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 11:22 上午
 **/
public interface TypeFilter {

    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory);
}