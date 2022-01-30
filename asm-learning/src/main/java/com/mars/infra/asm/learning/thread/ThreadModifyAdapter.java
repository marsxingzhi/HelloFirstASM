package com.mars.infra.asm.learning.thread;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by JohnnySwordMan on 2022/1/31
 */
public class ThreadModifyAdapter extends MethodVisitor {

    public ThreadModifyAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
