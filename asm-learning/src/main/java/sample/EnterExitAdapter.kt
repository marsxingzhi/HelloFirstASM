package sample

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2/2/22
 */
internal class EnterExitAdapter(api: Int, methodVisitor: MethodVisitor): MethodVisitor(api, methodVisitor) {

    override fun visitCode() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("method start")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            mv.visitLdcInsn("method end")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        }
        super.visitInsn(opcode)
    }

    /**
     * 虽然这个方法是最后调用的，标志方法体的结束，但是方法其实在这个方法执行之前都已经完成了。
     * 准确的讲，在执行return语句的时候，方法已经完成了，所以在visitMaxs方法中添加是不生效的
     */
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }
}