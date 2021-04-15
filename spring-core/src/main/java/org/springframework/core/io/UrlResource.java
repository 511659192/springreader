// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io;

import com.google.common.base.Objects;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 4:00 下午
 **/
public class UrlResource implements Resource {

    private URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public InputStream getInputStream() {

        try {
            URLConnection urlConnection = this.url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public File getFile() {
        try {
            URL url = this.url;
            URI uri = url.toURI();
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            return new File(schemeSpecificPart);
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
        UrlResource that = (UrlResource) o;
        return Objects.equal(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}