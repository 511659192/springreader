// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurablePropertyResolver;

import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:55 下午
 **/
@Slf4j
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

    @Override
    public String resolveRequiredPlaceholders(String text) {
        log.info("#resolveRequiredPlaceholders text:{}", text);
        return replacePlaceholders(text, this::getPropertyAsRawString);
    }

    private String replacePlaceholders(String text, Function<String, String> placeholderResolver) {
        return parseStringValue(text, placeholderResolver, Sets.newHashSet());
    }

    private String parseStringValue(String value, Function<String, String> placeholderResolver, Set<String> visited) {
        int startIdx = value.indexOf("${");
        if (startIdx == -1) {
            return value;
        }
        int endIdx = value.indexOf("}");
        String placeHolder = value.substring(startIdx + 2, endIdx);
        String result = placeholderResolver.apply(placeHolder);
        log.info("#parseStringValue placeHolder:{} result:{}", placeHolder, value);
        return result + value.substring(endIdx + 1);
    }


    protected abstract String getPropertyAsRawString(String key);
}