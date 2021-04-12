// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 8:11 下午
 **/
public interface ClassMetadata {
    String getClassName();
    boolean isIndependent();
    default boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }
    boolean isAbstract();
    boolean isInterface();
}