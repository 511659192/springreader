// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 7:06 下午
 **/
public class AntPathMatcher implements PathMatcher {

    private String pathSeparator = "/";

    @Override
    public boolean isPattern(String substring) {
        if (StringUtils.isBlank(substring)) {
            return false;
        }

        boolean placeHolder = false;
        for (int i = 0; i < substring.length(); i++) {
            char c = substring.charAt(i);
            if (c == '*' || c == '?') {
                return true;
            }

            if (c == '{') {
                placeHolder = true;
                continue;
            }

            if (c == ')' && placeHolder) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(String path, String pattern) {
        return true;
    }
}