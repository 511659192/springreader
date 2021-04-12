// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 2:48 下午
 **/
@Slf4j
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

    public AbstractXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        loadBeanDefinitions(beanDefinitionReader);
    }

    private void loadBeanDefinitions(XmlBeanDefinitionReader beanDefinitionReader) throws Exception {
        String[] configLocations = getConfigLocations();
        for (String configLocation : configLocations) {
            beanDefinitionReader.loadBeanDefinitions(configLocation);
        }
    }
}