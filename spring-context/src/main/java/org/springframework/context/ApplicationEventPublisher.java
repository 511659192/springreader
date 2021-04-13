// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/13 3:17 下午
 **/
@FunctionalInterface
public interface ApplicationEventPublisher {

    default void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }

    void publishEvent(Object event);
}