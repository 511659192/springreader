// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.type.classreading;

import org.objectweb.asm.Opcodes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/4/6 3:34 下午
 **/
public class SimpleAnnotationMetadata implements AnnotationMetadata {

    private final String className;
    private final int access;
    private final String enclosingClassName;
    private final String superClassName;
    private final boolean independentInnerClass;
    private final String[] interfaceNames;
    private final String[] memberClassNames;
    private final MethodMetadata[] annotatedMethods;
    private final MergedAnnotations annotations;

    public SimpleAnnotationMetadata(String className, int access, String enclosingClassName, String superClassName,
                                    boolean independentInnerClass, String[] interfaceNames, String[] memberClassNames,
                                    MethodMetadata[] annotatedMethods, MergedAnnotations annotations) {

        this.className = className;
        this.access = access;
        this.enclosingClassName = enclosingClassName;
        this.superClassName = superClassName;
        this.independentInnerClass = independentInnerClass;
        this.interfaceNames = interfaceNames;
        this.memberClassNames = memberClassNames;
        this.annotatedMethods = annotatedMethods;
        this.annotations = annotations;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean isIndependent() {
        return this.enclosingClassName == null || this.independentInnerClass;
    }

    @Override
    public boolean isAbstract() {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }

    @Override
    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return Arrays.stream(annotatedMethods).filter(methodMetadata -> methodMetadata.isAnnotated(annotationName))
                     .collect(Collectors.toSet());
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.annotations;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleAnnotationMetadata.class.getSimpleName() + "[", "]").add("className='" + className + "'").add(
                "access=" + access).add("enclosingClassName='" + enclosingClassName + "'").add("superClassName='" + superClassName + "'").add(
                "independentInnerClass=" + independentInnerClass).add("interfaceNames=" + Arrays.toString(interfaceNames)).add(
                "memberClassNames=" + Arrays.toString(memberClassNames)).add("annotatedMethods=" + Arrays.toString(annotatedMethods)).add(
                "annotations=" + annotations).toString();
    }
}