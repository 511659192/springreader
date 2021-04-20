// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyValues;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 10:56 上午
 **/
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {


    /**
     * bean实例化前使用,返回bean的代理,并阻断原始的实例化流程.
     *
     * 如果返回了一个非空数据,bean的创建过程将被短路.
     *
     * 适用的后续流程是{@link #postProcessAfterInitialization}回调
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    /**
     * 在填充属性值之前,通过构造方法和工作方法,优化实例化后的bean
     *
     * 在spring自身的自动装配之前,此回调可以很好的进行自定义属性值的注入
     * @param bean
     * @param beanName
     * @return
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    /**
     * 属性值后置处理
     * 如果使用了自定义的{@link #postProcessProperties} 或者 提供了{@code pvs}入参, 此接口需要返回为空
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     */
    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return null;
    }
}