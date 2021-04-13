// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.MethodMetadata.SimpleMethodMetadata;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static org.springframework.util.SpringAsmInfo.ASM_VERSION;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/4/4 22:30
 **/
public class SimpleAnnotationMetadataReadingVisitor extends ClassVisitor {

    Logger log = LoggerFactory.getLogger(getClass());
    private ClassLoader classLoader;
    private int access;
    private String className;
    @Nullable
    private String enclosingClassName;
    private boolean independentInnerClass;
    private String signature;
    private String superClassName;
    private String[] interfaceNames;
    private Source source;
    private List<MergedAnnotation<?>> annotations = new ArrayList<>();

    @Nullable
    private SimpleAnnotationMetadata metadata;
    private List<SimpleMethodMetadata> annotatedMethods;
    private Set<String> memberClassNames = new LinkedHashSet<>(4);


    public SimpleAnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        super(ASM_VERSION);
        this.classLoader = classLoader;
        log.info("");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        log.info("begin");
        log.info("className:{} name:{} signature:{} superName:{} interfaces:{}", className, name, signature, superName, interfaces);
        this.access = access;
        this.className = name.replaceAll("/", ".");
        this.superClassName = superName.replaceAll("/", ".");
        this.interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceNames[i] = interfaces[i].replaceAll("/", ".");
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        log.info("className:{} name:{} signature:{} descriptor:{} exceptions:{}", className, name, signature, descriptor, exceptions);
        // 桥接方法不处理
        if ((access & Opcodes.ACC_BRIDGE) == 0) {
            return null;
        }

        Consumer<SimpleMethodMetadata> consumer = this.annotatedMethods::add;
        SimpleMethodMetadataReadingVisitor methodMetadataReadingVisitor = new SimpleMethodMetadataReadingVisitor(this.classLoader, this.className,
                                                                                                                 access, name, descriptor, consumer);

        return methodMetadataReadingVisitor;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        log.info("className:{} descriptor:{} visible:{}", className, descriptor, visible);
        Consumer<MergedAnnotation<Annotation>> consumer = this.annotations::add;
        return MergedAnnotationReadingVisitor.get(this.classLoader, this::getSource, descriptor, visible, consumer);
    }

    @Override
    public void visitEnd() {
        log.info("className:{}", className);
        log.info("end \n");
        String[] memberClassNames = null;
        MethodMetadata[] annotatedMethods = null;
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        SimpleAnnotationMetadata annotationMetadata = new SimpleAnnotationMetadata(this.className, this.access, this.enclosingClassName,
                                                                                   this.superClassName, this.independentInnerClass,
                                                                                   this.interfaceNames, memberClassNames, annotatedMethods,
                                                                                   annotations);
        this.metadata = annotationMetadata;
    }

    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    private Source getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.className);
            this.source = source;
        }
        return source;
    }

    private static final class Source {
        private final String className;
        Source(String className) {
            this.className = className;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Source.class.getSimpleName() + "[", "]").add("className='" + className + "'").toString();
        }
    }
}