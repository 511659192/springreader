// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.ResourceLoaderCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.util.Arrays;
import java.util.function.BinaryOperator;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 4:06 下午
 **/
@Slf4j
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable, ResourceLoaderCapable {

    @Setter
    @Getter
    private Environment environment;

    @Getter
    private BeanDefinitionRegistry registry;

    @Setter
    @Getter
    private ResourceLoader resourceLoader;

    private BeanNameGenerator beanNameGenerator = BeanNameGenerator.DEFAULT_BEAN_NAME_GENERATOR;

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }


    public void loadBeanDefinitions(String... locations) throws Exception {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    private int loadBeanDefinitions(String location) throws Exception {
        Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
        BinaryOperator<Integer> accumulator = (a, b) -> a + b;
        int cnt = Arrays.stream(resources).map(resource -> loadBeanDefinitions(resource)).reduce(accumulator).get();
        return cnt;
    }
}