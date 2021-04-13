// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.env;

import java.util.Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 7:15 下午
 **/
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

    Map<String, Object> getSystemProperties();

    Map<String, Object> getSystemEnvironment();
}