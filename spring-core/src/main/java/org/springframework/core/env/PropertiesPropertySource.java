// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import java.util.Properties;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:35 下午
 **/
public class PropertiesPropertySource extends PropertySource<Properties> {

    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}