package com.mars.infra.asm.learning.example3;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/27
 * 方法替换
 */
class MethodReplaceAdapter extends MethodVisitor {

    public MethodReplaceAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    // 首先MethodVisitor本身就是处理方法体的，这里的visitMethodInsn可以理解成方法体内部的每一条方法治理
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (owner.equals("java/lang/Math") && name.equals("max") && descriptor.equals("(II)I")) {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(II)I", false);
        } else if (owner.equals("java/io/PrintStream") && name.equals("println") && descriptor.equals("(I)V")) {
            /**
             * 替换no-static方法，需要考虑是否处理this变量
             * 替换Instruction时，要保证操作数栈，在修改前后是一致的
             *
             * 如果将super.visitMethodInsn方法注释掉，那么表示这个Instruction写不到class中，但是非静态方法有一个this变量，这个需要考虑是否处理
             * 如果都注释掉，那么修改后的class文件会存在一个变量，即PrintStream var10000 = System.out;
             */
//            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mars/infra/asm/study/utils/ParameterUtils", "output", "(Ljava/io/PrintStream;I)V", false);
//            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mars/infra/asm/study/utils/ParameterUtils", "output", "(I)V", false);

            /**
             * 上述可以看到，如果都注释掉，那么class中会存在一个PrintStream类型的变量，为了保证修改前后，操作数栈是一致的，因此可以使用这个this变量，当做入参传入
             */
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mars/infra/asm/study/utils/ParameterUtils", "output", "(Ljava/io/PrintStream;I)V", false);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
