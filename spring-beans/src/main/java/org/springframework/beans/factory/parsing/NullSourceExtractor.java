// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;

import javax.annotation.Nullable;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 5:53 下午
 **/
public class NullSourceExtractor implements SourceExtractor{
    @Nullable
    @Override
    public Object extractSource(Object sourceCandidate, @Nullable Resource definingResource) {
        return null;
    }
}