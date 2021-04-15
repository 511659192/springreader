// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/2 3:12 下午
 **/
@Slf4j
public class SimpleMetadataReader implements MetadataReader {

    @Getter
    Resource resource;

    AnnotationMetadata annotationMetadata;

    @Getter
    ClassLoader classLoader;

    public SimpleMetadataReader(Resource resource, ClassLoader classLoader) {
//        log.info(resource + "");

        SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
        ClassReader classReader = getClassReader(resource);
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.resource = resource;
        this.annotationMetadata = visitor.getMetadata();

//        log.info("");
    }

    private ClassReader getClassReader(Resource resource) {
        ClassReader classReader;
        try (InputStream is = resource.getInputStream()) {
            classReader = new ClassReader(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classReader;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return this.annotationMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return annotationMetadata;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleMetadataReader.class.getSimpleName() + "[", "]").add("resource=" + resource).add(
                "annotationMetadata=" + annotationMetadata).add("classLoader=" + classLoader).toString();
    }
}