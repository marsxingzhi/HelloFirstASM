package tree.sample


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import tree.core.ClassTransformer


/**
 * Created by JohnnySwordMan on 2/11/22
 */
class ComputeTimerClassNode(api: Int, private val classVisitor: ClassVisitor?) : ClassNode(api) {


    override fun visitEnd() {

        AddTimerFieldTransformer(null).transform(this)

        super.visitEnd()

        classVisitor?.let { accept(it) }
    }

    private class AddTimerFieldTransformer(transformer: ClassTransformer?) :
        ClassTransformer(transformer), Opcodes {

        override fun transform(cn: ClassNode?) {

            var hasPresent = false
            cn?.fields?.forEach { node ->
                if (node.name == "timer" && node.desc == "J") {
                    hasPresent = true
                }
            }

            if (!hasPresent) {
                // or
                cn?.fields?.add(FieldNode(ACC_PUBLIC or ACC_STATIC, "timer", "J", null, null))
            }

            // 所有方法都加上
            cn?.methods?.forEach { methodNode ->
                if ("<init>" != methodNode.name && "<clinit>" != methodNode.name) {

                    val start = InsnList().apply {
                        // 注意：不是FieldNode
                        add(FieldInsnNode(Opcodes.GETSTATIC, cn.name, "timer", "J"))
                        add(MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"))
                        add(InsnNode(Opcodes.LSUB))
                        add(FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "timer", "J"))
                    }

                    // InsnList是双向链表，insert表示插入在开头，add表示添加在结尾
                    methodNode.instructions.takeIf { it.size() > 0 }?.insert(start)

                    // 结尾
                    val end = InsnList().apply {
                        add(FieldInsnNode(Opcodes.GETSTATIC, cn.name, "timer", "J"))
                        add(MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"))
                        add(InsnNode(Opcodes.LADD))
                        add(FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "timer", "J"))
                    }

                    // 找到 IRETURN和ATHROW的Instruction，在这个之前插入end
                    methodNode.instructions?.forEach { insnNode ->
                        val opcode = insnNode.opcode
                        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.ARETURN) || opcode == Opcodes.ATHROW) {
                            // 在insnNode之前插入end
                            methodNode.instructions.insertBefore(insnNode, end)
                        }
                    }





                    methodNode.maxStack += 4  // 注意：这个一定要写，不写的话，是无法生成class的
//                    methodNode.maxLocals 不变就不修改了

                }
            }

            super.transform(cn)
        }
    }

}