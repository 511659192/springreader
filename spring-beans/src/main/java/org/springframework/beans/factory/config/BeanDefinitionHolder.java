// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 8:00 下午
 **/
@Getter
@Setter
@Slf4j
public class BeanDefinitionHolder {

    private BeanDefinition beanDefinition;

    private String beanName;

    @Nullable
    @Getter
    private String[] aliases = new String[0];


    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
    }

    public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
//        log.info("beanName:{}", beanName);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BeanDefinitionHolder.class.getSimpleName() + "[", "]").add("beanDefinition=" + beanDefinition).add("beanName='" + beanName + "'")
                .add("aliases=" + Arrays.toString(aliases)).toString();
    }
}