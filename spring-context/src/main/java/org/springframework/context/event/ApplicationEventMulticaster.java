// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.event;

import org.springframework.context.ApplicationListener;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 3:54 下午
 **/
public interface ApplicationEventMulticaster {

    void addApplicationListener(ApplicationListener<?> applicationListener);

    void addApplicationListenerBean(String listenerBeanName);
}