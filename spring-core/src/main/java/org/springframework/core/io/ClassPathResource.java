// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io;

import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/1 7:30 下午
 **/
@Slf4j
public class ClassPathResource implements Resource {

    private String path;
    private ClassLoader classLoader;

    public ClassPathResource(String path, ClassLoader classLoader) {
        this.path = path;
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getInputStream() {
        return this.classLoader.getResourceAsStream(this.path);
    }

    @Override
    public URL getURL() {
        return this.classLoader.getResource(this.path);
    }

    @Override
    public File getFile() {
        URL url = getURL();
        try {
            return new File(url.toURI().getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClassPathResource that = (ClassPathResource) o;
        return Objects.equal(path, that.path) && Objects.equal(classLoader, that.classLoader);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}