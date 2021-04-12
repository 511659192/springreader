// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:46 下午
 **/
public class SystemEnvironmentPropertySource extends PropertySource<Map<String, String>> {

    public SystemEnvironmentPropertySource(String name, Map<String, String> source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}