// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
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

    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));


    @Override
    public Object getSingleton(String name) {
        return getSingleton(name, true);
    }

    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        if (!isSingletonCurrentlyInCreation(beanName)) {
            return null;
        }

        singletonObject = this.earlySingletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        if (!allowEarlyReference) {
            return null;
        }

        synchronized (this.singletonObjects) {
            singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject != null) {
                return singletonObject;
            }

            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject != null) {
                return singletonObject;
            }

            ObjectFactory factory = this.singletonFactories.get(beanName);
            if (factory != null) {
                singletonObject = factory.getObject();
                this.earlySingletonObjects.put(beanName, singletonObject);
                this.singletonFactories.remove(beanName);
            }
        }

        return singletonObject;
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
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

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException(
                        "Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object ["
                                + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (this.singletonObjects.containsKey(beanName)) {
                return;
            }

            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }
}