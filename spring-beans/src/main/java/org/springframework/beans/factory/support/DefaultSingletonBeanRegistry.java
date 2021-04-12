// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/23 3:37 下午
 **/
@Slf4j
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {


    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /** Cache of singleton factories: bean name to ObjectFactory. */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);


    @Override
    public Object getSingleton(String name) {
        Object singletonObject = this.singletonObjects.get(name);
        if (singletonObject != null) {
            return singletonObject;
        }

        synchronized (this.singletonObjects) {
            singletonObject = this.singletonObjects.get(name);
            if (singletonObject != null) {
                return singletonObject;
            }

            ObjectFactory<?> objectFactory = singletonFactories.get(name);
            if (objectFactory == null) {
                return null;
            }
            singletonObject = objectFactory.getObject();
            singletonFactories.remove(name);
            singletonObjects.put(name, singletonObject);


        }

        return singletonObject;
    }


    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject != null) {
                return singletonObject;
            }

            singletonObject = singletonFactory.getObject();
            addSingleton(beanName, singletonObject);
            return singletonObject;
        }
    }

    private void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }
}