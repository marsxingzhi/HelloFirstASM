package com.mars.infra.asm.learning.example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/26
 */
public class HelloWorldDump3 implements Opcodes {

    /**
     * public class HelloWorld {
     *      static {
     *
     *      }
     * }
     * JVM在编译Java代码时，如果没有默认构造函数，JVM会帮助我们自动生成默认构造函数
     * 但是在利用ASM写代码时，这个是需要我们自己去生成的。ASM操作的是class，这个时候早已经编译过了
     */
    public byte[] dump() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(V1_8,
                ACC_PUBLIC + ACC_SUPER,
                "generate/_HelloWorld",
                null,
                "java/lang/Object",
                null);

        {
            // <init>构造函数的名字
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

            methodVisitor.visitCode();
            // 调用父类的构造函数
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);

            methodVisitor.visitEnd();
        }

        {
            // 静态代码块
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitEnd();
        }

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
