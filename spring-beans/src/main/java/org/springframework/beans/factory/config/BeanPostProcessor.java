// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 10:57 上午
 **/
public interface BeanPostProcessor {

    /**
     * 在bean初始化回调(例如: {@link InitializingBean#afterPropertiesSet()} 或者 自定义的 init-method })之前
     * 使用{@link BeanPostProcessor}可以返回一个新的实例
     * 此实例是对原始实例的封装,并且已经填充完属性值
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }


    /**
     * 在bean初始化回调(例如: {@link InitializingBean#afterPropertiesSet()} 或者 自定义的 init-method })之后
     * 使用{@link BeanPostProcessor}可以返回一个新的实例
     * 此实例是对原始实例的封装,并且已经填充完属性值
     *
     *
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}