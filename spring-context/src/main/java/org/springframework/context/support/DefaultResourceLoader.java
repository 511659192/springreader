// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/25 5:51 下午
 **/
@Slf4j
public class DefaultResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String location) {

        log.info("location:{}", location);

        if (location.startsWith("/")) {
            return getResourceByPath(location);
        }

        if (location.startsWith("classpath:")) {
            return new ClassPathResource(location.substring("classpath:".length()), getClassLoader());
        }

        try {
            URL url = new URL(location);
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            return getResourceByPath(location);
        }
    }

    private Resource getResourceByPath(String location) {
        return new ClassPathContextResource(location, this.getClassLoader());
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected static class ClassPathContextResource extends ClassPathResource implements Resource {
        public ClassPathContextResource(String path, ClassLoader classLoader) {
            super(path, classLoader);
        }
    }

}