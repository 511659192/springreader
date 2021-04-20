// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.ClassUtils;
import org.springframework.util.SpringAsmInfo;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 22:37
 **/
public class MergedAnnotationReadingVisitor<T extends Annotation> extends AnnotationVisitor {
    @Nullable
    private ClassLoader classLoader;
    @Nullable
    private Object source;
    private Class<T> annotationType;
    private Consumer<MergedAnnotation<T>> consumer;
    private Map<String, Object> attributes = new LinkedHashMap<>(4);

    public MergedAnnotationReadingVisitor(@Nullable ClassLoader classLoader, @Nullable Object source,
            Class<T> annotationType, Consumer<MergedAnnotation<T>> consumer) {

        super(SpringAsmInfo.ASM_VERSION);
        this.classLoader = classLoader;
        this.source = source;
        this.annotationType = annotationType;
        this.consumer = consumer;
    }


    static <T extends Annotation> AnnotationVisitor get(@Nullable ClassLoader classLoader,
            @Nullable Supplier<Object> sourceSupplier, String descriptor, boolean visible,
            Consumer<MergedAnnotation<T>> consumer) {
        if (!visible) {
            return null;
        }

        final String typeName = Type.getType(descriptor).getClassName();
        final Object source = sourceSupplier.get();
        final Class<T> annotationType = ClassUtils.forName(typeName, false, classLoader);
        return new MergedAnnotationReadingVisitor<T>(classLoader, source, annotationType, consumer);
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            value = ((Type) value).getClassName();
        }

        this.attributes.put(name, value);
    }

    @Override
    public void visitEnd() {
        MergedAnnotation<T> annotation = MergedAnnotation.of(this.classLoader, this.source, this.annotationType, this.attributes);
        this.consumer.accept(annotation);
    }
}