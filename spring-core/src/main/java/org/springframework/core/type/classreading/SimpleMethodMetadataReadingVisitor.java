// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.objectweb.asm.MethodVisitor;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.SpringAsmInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 3:54 下午
 **/
public class SimpleMethodMetadataReadingVisitor extends MethodVisitor {
    @Nullable
    private final ClassLoader classLoader;
    private final String declaringClassName;
    private final int access;
    private final String name;
    private final String descriptor;
    private final Consumer<MethodMetadata.SimpleMethodMetadata> consumer;

    SimpleMethodMetadataReadingVisitor(@Nullable ClassLoader classLoader, String declaringClassName,
            int access, String name, String descriptor, Consumer<MethodMetadata.SimpleMethodMetadata> consumer) {

        super(SpringAsmInfo.ASM_VERSION);
        this.classLoader = classLoader;
        this.declaringClassName = declaringClassName;
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.consumer = consumer;
    }
}