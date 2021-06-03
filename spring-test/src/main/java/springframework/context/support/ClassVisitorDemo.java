// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/5/18 22:36
 **/
public class ClassVisitorDemo extends ClassVisitor {

    public ClassVisitorDemo() {
        super(Opcodes.ASM7);
    }

    public ClassVisitorDemo(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }
}