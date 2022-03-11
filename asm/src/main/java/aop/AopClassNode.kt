package aop

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import tree.core.MethodTransformer

/**
 * Created by Mars on 2022/3/11
 *
 * aop简单示例
 *
 * 1. 第一遍：visit class时，先收集注解的信息。只是reader，没有writer
 * 2. 第二遍：visit class时，比较方法体内的InsnNode的属性是否与注解的信息一致，如果一致，可替换
 */
class AopClassNode(private val classVisitor: ClassVisitor?) : ClassNode(Opcodes.ASM9) {

    override fun visitEnd() {
        val aopTransformer = AopTransformer(null)
        methods.filter {
            (it.access and Opcodes.ACC_NATIVE) == 0 && (it.access and Opcodes.ACC_ABSTRACT) == 0
        }.forEach {
            aopTransformer.transform(it)
        }
        super.visitEnd()
        classVisitor?.let { accept(it) }
    }

    /**
     * 两种方式？
     * 方式一：替换原先的InsnNode中的name、desc字段
     * 方式二：生成一个新的InsnNode，替换原先的InsnNode
     */
    private class AopTransformer(mt: MethodTransformer?) : MethodTransformer(mt) {
        override fun transform(node: MethodNode?) {
            super.transform(node)
            // 精准定位
            if (node?.name == "sayHello" && node.desc == "()V") {

                node.instructions.asIterable().forEach {
                    when (it) {
                        is MethodInsnNode -> {
                            var curInsnNode = it
                            if (it.owner == "aop/Log" && it.name == "e" && it.desc == "(Ljava/lang/String;Ljava/lang/String;)V") {
                                // 方案一：
//                                modifyWithDirectly(it)
                                // 方案二：
                                modifyV2(it, node)
                            }
                        }
                        is TypeInsnNode -> {

                        }
                        is FieldInsnNode -> {

                        }
                    }
                }
            }

        }

        /**
         * 方案二：
         * 生成一个新的InsnNode，替换原先的
         */
        private fun modifyV2(
            it: MethodInsnNode,
            node: MethodNode
        ) {
            val newMethodInsnNode = MethodInsnNode(
                it.opcode,
                "aop/Logger",
                "ee",
                "(Ljava/lang/String;Ljava/lang/String;)V"
            )
            node.instructions.insert(it, newMethodInsnNode)
            node.instructions.remove(it)  // 看起来内部自己处理了链表的删除逻辑
        }

        /**
         * 方案一：
         * 直接修改当前InsnNode的属性内容
         */
        private fun modifyWithDirectly(it: MethodInsnNode) {
            it.owner = "aop/Logger"
            it.name = "ee"
            it.desc = "(Ljava/lang/String;Ljava/lang/String;)V"
        }
    }
}