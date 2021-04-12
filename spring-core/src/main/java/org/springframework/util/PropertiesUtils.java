// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.util;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/3/29 23:24
 **/
public abstract class PropertiesUtils {

    public static Properties loadAllProperties(String resourceName, @Nullable ClassLoader classLoader) {
        try {
            ClassLoader using = Optional.ofNullable(classLoader).orElse(Thread.currentThread().getContextClassLoader());
            Enumeration<URL> urls = using != null ? using.getResources(resourceName) : ClassLoader.getSystemResources(resourceName);
            Properties properties = new Properties();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                URLConnection urlConnection = url.openConnection();
                try (InputStream is = urlConnection.getInputStream()) {
                    if (resourceName.endsWith(".xml")) {
                        properties.loadFromXML(is);
                    } else {
                        properties.load(is);
                    }
                }
            }

            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Map<K, V> prop2Map(Properties properties) {
        if (properties == null) {
            return Collections.emptyMap();
        }
        Map<K, V> result = Maps.newHashMap();
        properties.forEach((key, val) -> result.put((K) key, (V) val));
        return result;
    }
}