// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.event;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationListener;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/15 4:31 下午
 **/
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware, BeanClassLoaderAware {

    final Map<ListenerCacheKey, CachedListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);
    private final DefaultListenerRetriever defaultRetriever = new DefaultListenerRetriever();


    @Override
    public void addApplicationListener(ApplicationListener<?> applicationListener) {
        synchronized (this.defaultRetriever) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(applicationListener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(applicationListener);
            }

            this.defaultRetriever.applicationListeners.add(applicationListener);
            this.retrieverCache.clear();
        }
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {
        @Override
        public int compareTo(ListenerCacheKey o) {
            return 0;
        }
    }

    private class CachedListenerRetriever {
        @Nullable
        public volatile Set<ApplicationListener<?>> applicationListeners;

        @Nullable
        public volatile Set<String> applicationListenerBeans;
    }

    private class DefaultListenerRetriever {

        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

        public final Set<String> applicationListenerBeans = new LinkedHashSet<>();
    }


}