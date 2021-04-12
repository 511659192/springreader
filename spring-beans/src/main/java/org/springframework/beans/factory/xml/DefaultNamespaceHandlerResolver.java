// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.beans.factory.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Properties;

import static org.springframework.util.PropertiesUtils.loadAllProperties;
import static org.springframework.util.PropertiesUtils.prop2Map;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/26 5:19 下午
 **/
@Slf4j
public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver{

    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring2.handlers";

    private final String handlerMappingsLocation;

    private final ClassLoader classLoader;

    private volatile Map<String, Object> handlerMappings;

    public DefaultNamespaceHandlerResolver(ClassLoader classLoader) {
        this(classLoader, DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    public DefaultNamespaceHandlerResolver(ClassLoader classLoader, String handlerMappingsLocation) {
        this.handlerMappingsLocation = handlerMappingsLocation;
        this.classLoader = classLoader;
        log.info("handlerMappingsLocation:{}", handlerMappingsLocation);
    }

    @Override
    public NamespaceHandler resolve(String namespaceUri) {
        final Map<String, Object> handlerMappings = getHandlerMappings();
        Object o = handlerMappings.get(namespaceUri);
        if (o == null) {
            throw new RuntimeException("no handler for:" + namespaceUri);
        }

        String className = ((String) o);

        Class<NamespaceHandler> clazz = ClassUtils.forName(className, false, this.classLoader);
        NamespaceHandler namespaceHandler = ClassUtils.instantiateClass(clazz);
        return namespaceHandler;
    }

    public Map<String, Object> getHandlerMappings() {
        Map<String, Object> handlerMappings = this.handlerMappings;
        if (handlerMappings == null) {
            synchronized (this) {
                if (handlerMappings == null) {
                    Properties mapping = loadAllProperties(this.handlerMappingsLocation, this.classLoader);
                    this.handlerMappings = prop2Map(mapping);
                }
            }
        }

        return this.handlerMappings;
    }
}