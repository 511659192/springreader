// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.parsing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/7 7:13 下午
 **/
@Slf4j
public class BeanComponentDefinition extends BeanDefinitionHolder implements ComponentDefinition {

    private BeanDefinition[] innerBeanDefinitions;
    private BeanReference[] beanReferences;


    public BeanComponentDefinition(BeanDefinitionHolder beanDefinitionHolder) {
        super(beanDefinitionHolder);

        List<BeanDefinition> innerBeans = new ArrayList<>();
        List<BeanReference> references = new ArrayList<>();
        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                innerBeans.add(((BeanDefinitionHolder) value).getBeanDefinition());
            }
            else if (value instanceof BeanDefinition) {
                innerBeans.add((BeanDefinition) value);
            }
            else if (value instanceof BeanReference) {
                references.add((BeanReference) value);
            }
        }
        this.innerBeanDefinitions = innerBeans.toArray(new BeanDefinition[0]);
        this.beanReferences = references.toArray(new BeanReference[0]);

        log.info("beanName:{}", this.getBeanName());
    }
}