// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:29 下午
 **/
public interface AutowireCapableBeanFactory extends BeanFactory {

    int AUTOWIRE_NO = 0;

    int AUTOWIRE_BY_NAME = 1;

    int AUTOWIRE_BY_TYPE = 2;

    int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;


    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter);
}