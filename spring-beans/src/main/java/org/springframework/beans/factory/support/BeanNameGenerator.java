// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

import java.beans.Introspector;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 11:04 上午
 **/
public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);
    BeanNameGenerator DEFAULT_BEAN_NAME_GENERATOR = new DefaultBeanNameGenerator();
    BeanNameGenerator ANNOTATION_BEAN_NAME_GENERATOR = new AnnotationBeanNameGenerator();

    class DefaultBeanNameGenerator implements BeanNameGenerator {

        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            return fromFullClassName(definition.getBeanClassName());
        }

    }


    class AnnotationBeanNameGenerator implements BeanNameGenerator {

        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            return buildDefaultBeanName(definition, registry);
        }

        private String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
            return fromFullClassName(definition.getBeanClassName());
        }
    }

    default String fromFullClassName(String fullClassName) {
        String substring = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return Introspector.decapitalize(substring);
    }
}