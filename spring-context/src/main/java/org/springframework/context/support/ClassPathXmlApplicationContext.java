// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 11:54 上午
 **/
@Slf4j
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[] {configLocation}, true, null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) {
        super(parent);

        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }
}