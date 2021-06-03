// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/5/18 22:32
 **/
@Slf4j
public class ClassReaderDemo {

    public static void main(String[] args) throws Exception {

        String canonicalName = "springframework.context.support.OutterClass";
        ClassReader classReader = new ClassReader(canonicalName);
        ClassParser classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        String descriptor = classParser.descriptor;

        System.out.println(descriptor);

        canonicalName = "springframework.context.support.OutterClass$1";
        classReader = new ClassReader(canonicalName);
        classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        descriptor = classParser.descriptor;
        System.out.println(descriptor);

        canonicalName = "springframework.context.support.OutterClass$2";
        classReader = new ClassReader(canonicalName);
        classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        descriptor = classParser.descriptor;
        System.out.println(descriptor);

        canonicalName = "springframework.context.support.OutterClass$3";
        classReader = new ClassReader(canonicalName);
        classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        descriptor = classParser.descriptor;
        System.out.println(descriptor);

        canonicalName = "springframework.context.support.OutterClass$InnerClass";
        classReader = new ClassReader(canonicalName);
        classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        descriptor = classParser.descriptor;
        System.out.println(descriptor);

        canonicalName = "springframework.context.support.OutterClass$InnerClass2";
        classReader = new ClassReader(canonicalName);
        classParser = new ClassParser();
        classReader.accept(classParser, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        descriptor = classParser.descriptor;
        System.out.println(descriptor);

    }

    // ClassParser
    static class ClassParser extends ClassVisitor {

        String descriptor;

        public ClassParser() {
            super(ASM7);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            log.info("visit name:{} signature:{} superName:{} interfaces:{}", name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            log.info("visitField name:{} descriptor:{} signature:{} value:{]", name, descriptor, signature, value);
            return new FieldParser();
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            log.info("visitInnerClass name:{} outerName:{} innerName:{}", name, outerName, innerName);
            super.visitInnerClass(name, outerName, innerName, access);
        }

        @Override
        public void visitOuterClass(String owner, String name, String descriptor) {
            log.info("visitOuterClass owner:{} name:{} descriptor:{}", owner, name, descriptor);
            super.visitOuterClass(owner, name, descriptor);
        }
    }

    // FieldParser
    static class FieldParser extends FieldVisitor {

        public FieldParser() {
            super(ASM7);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            System.out.println("注释: " + descriptor + " 可见性: " + visible);
            return new AnnotationParser();
        }
    }

    // AnnotationParser
    static class AnnotationParser extends AnnotationVisitor {

        public AnnotationParser() {
            super(ASM7);
        }
    }
}