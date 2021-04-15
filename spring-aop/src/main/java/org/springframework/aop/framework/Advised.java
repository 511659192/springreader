// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.framework;

import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 4:21 下午
 **/
public interface Advised extends TargetClassAware {

    TargetSource getTargetSource();
}