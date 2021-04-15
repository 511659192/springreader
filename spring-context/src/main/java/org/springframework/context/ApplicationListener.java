// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context;

import java.util.EventListener;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 3:59 下午
 **/
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    void onApplicationEvent(E event);
}