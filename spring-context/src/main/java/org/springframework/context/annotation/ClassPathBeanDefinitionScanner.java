// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.annotation;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourceLoader;
import org.springframework.util.JsonUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 2:49 下午
 **/
@Slf4j
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private BeanDefinitionRegistry registry;

    private BeanNameGenerator beanNameGenerator = BeanNameGenerator.ANNOTATION_BEAN_NAME_GENERATOR;

    BeanDefinitionDefaults definitionDefaults;

    @Setter
    private BeanDefinitionDefaults beanDefinitionDefaults;

    @Setter
    private String[] autowireCandidatePatterns;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        this.registry = registry;
        setEnvironment(environment);
        setResourceLoader(resourceLoader);

        if (useDefaultFilters) {
            registerDefaultFilters();
        }
//        log.info("");
    }


    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
//        log.info("packages:{}", JsonUtils.toJson(basePackages));
        if (basePackages.length == 0) {
            return Collections.emptySet();
        }

        Set<BeanDefinitionHolder> allBeanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> definitions = findCandidateComponents(basePackage);
            definitions.forEach(candidate -> {
                String beanName = beanNameGenerator.generateBeanName(candidate, registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                BeanDefinitionHolder holder = new BeanDefinitionHolder(candidate, beanName);
                allBeanDefinitions.add(holder);
                registerBeanDefinition(holder, registry);

            });
        }

        return allBeanDefinitions;

    }

    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.definitionDefaults);
    }

}