package com.mars.infra.asm.learning.example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/25
 */
public class HelloWorldDump implements Opcodes {

    public byte[] dump() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        createClass(classWriter);
        createConstructor(classWriter);
        createMethod(classWriter);
        createMethod2(classWriter);
        createMethod3(classWriter);
        createMethod4(classWriter);
        createMethod5(classWriter);

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    /**
     * 生成try-catch语句
     * public void generateTryCatch() {
     *     try {
     *         int value = 0;
     *     } catch(Exception e) {
     *         e.printStackTrace()
     *     }
     * }
     */
    private void createMethod5(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "generateTryCatch", "()V", null, null);
        methodVisitor.visitCode();

        Label startLabel = new Label();
        Label endLabel = new Label();
        Label exceptionHandlerLabel = new Label();
        Label returnLabel = new Label();

        {
            methodVisitor.visitTryCatchBlock(startLabel, endLabel, exceptionHandlerLabel, "java/lang/Exception");
        }

        {
            methodVisitor.visitLabel(startLabel);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 1);
        }

        {
            methodVisitor.visitLabel(endLabel);
            methodVisitor.visitJumpInsn(GOTO, returnLabel);
        }

        {
            methodVisitor.visitLabel(exceptionHandlerLabel);
            methodVisitor.visitVarInsn(ASTORE, 1);
        }

        {
            methodVisitor.visitLabel(returnLabel);
            methodVisitor.visitInsn(RETURN);
        }

        {
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
    }

    /**
     * 生成for语句
     * public void generateFor() {
     *     for (int i = 0; i < 10; i++) {
     *         System.out.println(i);
     *     }
     * }
     */
    private void createMethod4(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "generateFor", "()V", null, null);

        methodVisitor.visitCode();
        {
            // 将int型的0值压入操作数栈；如果int c = 1；那么就是ICONST_1
            methodVisitor.visitInsn(ICONST_0);
            // 要生成的方法是一个实例方法，不是静态方法，因此在局部变量表中，索引为0的位置会保存该方法所在类的引用即this，如果是静态方法，则是0
            methodVisitor.visitVarInsn(ISTORE, 1);
        }

        Label label0 = new Label();
        Label returnLabel = new Label();

        {
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitIntInsn(BIPUSH, 10);
            methodVisitor.visitJumpInsn(IF_ICMPGE, returnLabel);
            methodVisitor.visitIincInsn(1, 1);
            methodVisitor.visitJumpInsn(GOTO, label0);
        }

        {
            methodVisitor.visitLabel(returnLabel);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
        }
        methodVisitor.visitEnd();
    }

    /**
     * 循环生成10个分支
     * <p>
     * 只有调用methodVisitor的visitXX方法，才会将代码写入，因此if、for、Label[] labels = new Label[10]等逻辑是不会写到生成的代码中的
     */
    private void createMethod3(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "testSwitch", "(I)V", null, null);

        Label[] labels = new Label[10];
        Label defaultLabel = new Label();
        Label returnLabel = new Label();

        for (int i = 0; i < 10; i++) {
            labels[i] = new Label();
        }
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ILOAD, 1);
        // switch语句特有的
        methodVisitor.visitTableSwitchInsn(1, 10, defaultLabel, labels);

        for (int i = 0; i < 10; i++) {
            methodVisitor.visitLabel(labels[i]);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("val---☺️ = " + i);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, returnLabel);
        }

        methodVisitor.visitLabel(defaultLabel);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn("val is unknown");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        methodVisitor.visitLabel(returnLabel);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);

        methodVisitor.visitEnd();
    }

    private void createMethod2(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "test1", "(I)V", null, null);

        Label caseLabel1 = new Label();
        Label caseLabel2 = new Label();
        Label defaultLabel = new Label();
        Label returnLabel = new Label();

        {
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitTableSwitchInsn(1, 2, defaultLabel, new Label[]{caseLabel1, caseLabel2});
        }

        {
            methodVisitor.visitLabel(caseLabel1);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("val = 1");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, returnLabel);
        }

        {
            methodVisitor.visitLabel(caseLabel2);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("val = 2");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, returnLabel);
        }

        {
            methodVisitor.visitLabel(defaultLabel);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("val is unknown");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        {
            methodVisitor.visitLabel(returnLabel);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(0, 0);
        }

        methodVisitor.visitEnd();
    }

    private void createMethod(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "test", "(I)V", null, null);

        Label elseLabel = new Label();
        Label returnLabel = new Label();

        methodVisitor.visitCode();

        {
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitJumpInsn(IFNE, elseLabel);
            // System的静态变量out，描述符即类名
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "L/java/io/PrintStream;");
            // 加载常量
            methodVisitor.visitLdcInsn("value is 0");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, returnLabel);
        }

        {
            // visitLabel方法确定label的位置
            methodVisitor.visitLabel(elseLabel);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("value is not 0");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        {
            methodVisitor.visitLabel(returnLabel);
            methodVisitor.visitInsn(RETURN);
        }
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    private void createClass(ClassWriter classWriter) {
        classWriter.visit(
                V1_8,
                ACC_PUBLIC,
                "generate/_HelloWorld",
                null,
                "java/lang/Object",
                null);
    }

    private void createConstructor(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

        // 开始方法体内部，只调用一次
        methodVisitor.visitCode();

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);

        // 结束方法体内部，只调用一次；注意：由于ClassWriter设置的是COMPUTE_FRAMES，这个标志位表示会自动计算maxStack、maxLocals等
        // 也就是说，visitMaxs方法的参数值写错了，也是可以生成文件
        methodVisitor.visitMaxs(1, 1);
        // 这个表示方法的结束，只调用一次
        methodVisitor.visitEnd();
    }

}
