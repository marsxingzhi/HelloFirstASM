package tree.sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import tree.core.ClassTransformer

/**
 * Created by JohnnySwordMan on 2/11/22
 */
class MethodAddClassNode(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val addMethodName: String?,
    private val addMethodDesc: String?): ClassNode(api) {

    override fun visitEnd() {
        val transformer = MethodAddTransformer(null, addMethodName, addMethodDesc)
        transformer.transform(this)

        super.visitEnd()

        classVisitor?.let { accept(it) }
    }

    internal class MethodAddTransformer(
        transformer: ClassTransformer?,
        private val addMethodName: String?,
        private val addMethodDesc: String?): ClassTransformer(transformer), Opcodes {

        override fun transform(cn: ClassNode?) {
            var hasPresent = false
            cn?.methods?.forEach { methodNode: MethodNode ->
                if (methodNode.name == addMethodName && methodNode.desc == addMethodDesc) {
                    hasPresent = true
                }
            }
            if (!hasPresent) {
                val methodNode = MethodNode().apply {
                    access = ACC_PUBLIC
                    name = addMethodName
                    desc = addMethodDesc

                    // 添加方法体，就是将每一条Instruction添加到集合中
//                    instructions.add()
//
//                    AbstractInsnNode
                    instructions.add(VarInsnNode(ILOAD, 1))
                    instructions.add(VarInsnNode(ILOAD, 2))
                    instructions.add(InsnNode(IMUL))
                    instructions.add(InsnNode(IRETURN))

                    maxStack = 2
                    maxLocals = 3

                }

                cn?.methods?.add(methodNode)
            }
            super.transform(cn)
        }
    }
}