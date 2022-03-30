package aop

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.*


/**
 * Created by Mars on 2022/3/20
 *
 * 实现了将某个MethodNode内容写到某个方法中
 */
class TestClassVisitor(classVisitor: ClassVisitor): ClassVisitor(Opcodes.ASM9, classVisitor) {


    override fun visitEnd() {
        super.visitEnd()

        /**
         * 这样只是生成一个方法名，方法体内容没有生成
         */
        val mv = cv.visitMethod(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC,
            "generateMethodNameXXX", "(Ljava/lang/String;Ljava/lang/String;)I", null, null
        )

        buildMock().accept(mv)

    }


    private fun buildMock(): MethodNode {

        val methodNode = MethodNode(
            ACC_PUBLIC or ACC_STATIC,
            "superE",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            null,
            null
        )

        val il = methodNode.instructions
        il.add(VarInsnNode(Opcodes.ALOAD, 0))
        il.add(TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"))
        il.add(InsnNode(Opcodes.DUP))
        il.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false))
        il.add(VarInsnNode(Opcodes.ALOAD, 1))
        il.add(
            MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
        )
        il.add(LdcInsnNode(" ---> invoke by Logger-------"))
        il.add(
            MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
        )
        il.add(
            MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false
            )
        )
        il.add(
            MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "aop/Log",
                "e",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                false
            )
        )
        il.add(InsnNode(Opcodes.ICONST_M1))
        il.add(InsnNode(Opcodes.IRETURN))

        methodNode.maxStack = 3
        methodNode.maxLocals = 2
        return methodNode
    }

}