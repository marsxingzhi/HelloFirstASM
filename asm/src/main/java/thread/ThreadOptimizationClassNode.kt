package thread

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import tree.core.MethodTransformer

/**
 * Created by JohnnySwordMan on 2/25/22
 */
class ThreadOptimizationClassNode(private val classVisitor: ClassVisitor?) :
    ClassNode(Opcodes.ASM9) {

    override fun visitEnd() {

        val methodTransformer = ThreadOptimizationMethodTransformer(null)
        methods.forEach { methodNode ->
            methodTransformer.transform(methodNode)
        }

        super.visitEnd()
        classVisitor?.let {
            accept(it)
        }
    }

    private class ThreadOptimizationMethodTransformer(mt: MethodTransformer?) :
        MethodTransformer(mt) {

        /**
         * 在该方法中找到new Thread的相关指令
         * 然后在Thread的构造方法之前插入一个ldc指令，加载字符串到操作数栈，这个字符串就是创建线程的类名
         */
        override fun transform(node: MethodNode?) {
            super.transform(node)
            node ?: return
            println(">>> 处理当前 ${node.name} 方法---start")

            // 如果未初始化，过滤FieldInsnNode，是没有输出的
//            node.instructions.filterIsInstance(MethodInsnNode::class.java).forEach {
//                println("指令 ${it.owner}---${it.name}---${it.desc}")
//                when (it.opcode) {
//
//                }
//            }
            // 遍历过程中处理节点，需要使用迭代器
            node.instructions.asIterable().forEach {
                when (it.opcode) {
                    Opcodes.INVOKESPECIAL -> {
                        (it as MethodInsnNode).transformInvokeSpecial(node)
                    }
                    Opcodes.NEW -> {
                        (it as TypeInsnNode).transformInvokeNew(node)
                    }
                }
            }

            println("======================================")
        }

        // 处理构造函数
        private fun MethodInsnNode.transformInvokeSpecial(methodNode: MethodNode) {
            when (this.owner) {
                THREAD -> {
                    if (this.desc == "(Ljava/lang/Runnable;)V") {
                        this.desc = "(Ljava/lang/Runnable;Ljava/lang/String;)V"
//                        this.owner = SHADOW_THREAD  在这里替换没用！需要在NEW指令出修改
                        methodNode.instructions.insertBefore(this, LdcInsnNode("module-asm"))
                    }
                }
            }
        }

        private fun TypeInsnNode.transformInvokeNew(node: MethodNode) {
            when (this.desc) {
                THREAD -> {
                    this.desc = SHADOW_THREAD
//                    this.find {
//                        it.opcode == Opcodes.INVOKESPECIAL
//                                && (it is MethodInsnNode && this.desc == it.owner && it.name == "<init>")
//                    }?.let {
//                        val invokeSpecialInsnNode = it as MethodInsnNode
//                        invokeSpecialInsnNode.owner = SHADOW_THREAD
//                        invokeSpecialInsnNode.desc = "(Ljava/lang/Runnable;Ljava/lang/String;)V"
//                    }
                }
            }
        }
    }
}