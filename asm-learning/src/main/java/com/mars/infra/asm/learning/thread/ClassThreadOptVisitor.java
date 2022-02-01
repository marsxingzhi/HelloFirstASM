package com.mars.infra.asm.learning.thread;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by JohnnySwordMan on 2022/1/31
 */
public class ClassThreadOptVisitor extends ClassVisitor {


    public ClassThreadOptVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new ThreadModifyAdapter(api, mv);
    }
}
