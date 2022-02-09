package com.mars.infra.asm.learning.example3;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * Created by JohnnySwordMan on 2022/1/26
 *
 * 在方法开始和结束的地方添加语句
 */
public class MethodEnterExitAdapter extends MethodVisitor {


    public MethodEnterExitAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    /**
     * 标志方法体的开始
     * 先处理插入代码，再执行父类方法
     */
    @Override
    public void visitCode() {
        /**
         * 哈哈，那怪没有插入成功。这个是字节码操作啊
         * 所有的代码需要通过ClassWriter才能最终插入成功
         */
        System.out.println("开始了！");

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("开始了！☺️");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        // 异常退出，正常退出
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("结束！☺️");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }

    /**
     * 虽然这个方法是最后调用的，标志方法体的结束，但是方法其实在这个方法执行之前都已经完成了。
     * 准确的讲，在执行return语句的时候，方法已经完成了，所以在visitMaxs方法中添加是不生效的
     */
    // 标志方法体的结束
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
//        System.out.println("结束了！");
        super.visitMaxs(maxStack, maxLocals);
    }
}
