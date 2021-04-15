// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 4:24 下午
 **/
public interface TargetSource extends TargetClassAware {

    Object getTarget();
}