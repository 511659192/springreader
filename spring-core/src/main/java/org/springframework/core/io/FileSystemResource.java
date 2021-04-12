// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/31 7:51 下午
 **/
public class FileSystemResource implements Resource {

    @Getter
    File file;

    Path filePath;

    public FileSystemResource(File file) {
        this.file = file;
        this.filePath = file.toPath();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return Files.newInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL getURL() {
        try {
            return file.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FileSystemResource.class.getSimpleName() + "[", "]").add("file=" + file).add(
                "filePath=" + filePath).toString();
    }
}