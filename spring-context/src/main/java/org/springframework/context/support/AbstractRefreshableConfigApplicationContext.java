// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.JsonUtils;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:48 下午
 **/
@Setter
@Getter
@Slf4j
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext {

    private String[] configLocations;

    public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public void setConfigLocations(String[] configLocations) {
        log.info("configLocations:{}", JsonUtils.toJson(configLocations));
        if (configLocations == null) {
            return;
        }

        String[] paths = new String[configLocations.length];
        for (int i = 0; i < configLocations.length; i++) {
            paths[i] = resolvePath(configLocations[i]);
        }
        this.configLocations = paths;
    }

    private String resolvePath(String path) {
        return getEnvironment().resolveRequiredPlaceholders(path);
    }
}