// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.springframework.aop.target.SingletonTargetSource;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 4:20 下午
 **/
public abstract class AopProxyUtils {

    public static Object getSingletonTarget(Object candidate) {
        if (candidate instanceof Advised) {
            TargetSource targetSource = ((Advised) candidate).getTargetSource();
            if (targetSource instanceof SingletonTargetSource) {
                return targetSource.getTarget();
            }
        }

        return null;
    }

}