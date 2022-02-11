package tree.sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import tree.core.ClassTransformer
import tree.core.MethodTransformer

/**
 * Created by JohnnySwordMan on 2/12/22
 */
class RemoveCodeClassNode(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val methodName: String?,
    private val methodDesc: String?): ClassNode(api) {

    override fun visitEnd() {
//        RemoveCodeTransformer(null, methodName, methodDesc).transform(this)
        val methodTransformer = RemoveCodeTransformerV2(null)
        methods?.forEach { node ->
            if (node.name == methodName && node.desc == methodDesc) {
                methodTransformer.transform(node)
            }
        }

        super.visitEnd()

        classVisitor?.let { accept(it) }
    }

    // 这个示例是处理方法体内的逻辑，因此使用MethodTransformer更为合理
    private class RemoveCodeTransformerV2(transformer: MethodTransformer?): MethodTransformer(transformer) {

        override fun transform(methodNode: MethodNode?) {
            methodNode?:return

            val iterator = methodNode.instructions.iterator()
            while (iterator.hasNext()) {
                val node1 = iterator.next()
                if (node1 != null && node1 is VarInsnNode && node1.`var` == 0) {
                    val node2 = node1.next
                    if (node2 != null && node2 is VarInsnNode && node2.`var` == 0) {
                        val node3 = node2.next
                        if ((node3 != null && node3 is FieldInsnNode && node3.opcode == Opcodes.GETFIELD)) {
                            val node4 = node3.next
                            if (node4 != null && node4 is FieldInsnNode && node4.opcode == Opcodes.PUTFIELD) {
                                if (node3.name == node4.name) {
                                    // 循环开始时iterator处于node1位置，将指针指向node4下一个节点
                                    while (iterator.next() != node4) {

                                    }
                                    methodNode.instructions.remove(node1)
                                    methodNode.instructions.remove(node2)
                                    methodNode.instructions.remove(node3)
                                    methodNode.instructions.remove(node4)
                                }
                            }
                        }
                    }
                }
            }

            super.transform(methodNode)
        }
    }

    /**
     * 移除this.val = this.val，该行代码对应的Instruction如下：
     * aload_0
     * aload_0
     * getfield
     * putfield
     *
     */
    private class RemoveCodeTransformer(
        transformer: ClassTransformer?,
        private val methodName: String?,
        private val methodDesc: String?): ClassTransformer(transformer) {

        override fun transform(cn: ClassNode?) {
            cn?.methods?.forEach { methodNode ->
                if (methodNode.name == methodName && methodNode.desc == methodDesc) {

                    val iterator = methodNode.instructions.iterator()
                    while (iterator.hasNext()) {
                        val node1 = iterator.next()
                        if (node1 != null && node1 is VarInsnNode && node1.`var` == 0) {
                            val node2 = node1.next
                            if (node2 != null && node2 is VarInsnNode && node2.`var` == 0) {
                                val node3 = node2.next
                                if ((node3 != null && node3 is FieldInsnNode && node3.opcode == Opcodes.GETFIELD)) {
                                    val node4 = node3.next
                                    if (node4 != null && node4 is FieldInsnNode && node4.opcode == Opcodes.PUTFIELD) {
                                        if (node3.name == node4.name) {
                                            // 循环开始时iterator始终处于node1位置，
                                            while (iterator.next() != node4) {

                                            }
                                            methodNode.instructions.remove(node1)
                                            methodNode.instructions.remove(node2)
                                            methodNode.instructions.remove(node3)
                                            methodNode.instructions.remove(node4)
                                        }
                                    }
                                }
                            }
                        }
                    }


//                    val deleteList = InsnList()
                    // 方法体内的Instruction集合
//                    methodNode.instructions?.forEach { insnNode ->
//                        val node1 = insnNode
//                        if (node1 is VarInsnNode && node1.`var` == 0) {
//                            val node2 = node1.next
//                            if (node2 != null && node2 is VarInsnNode && node2.`var` == 0) {
//                                val node3 = node2.next
//                                if (node3 != null && node3 is FieldInsnNode && node3.opcode == Opcodes.GETFIELD) {
//                                    val node4 = node3.next
//                                    if (node4 != null && node4 is FieldInsnNode && node4.opcode == Opcodes.PUTFIELD) {
//                                        // 这里需要判断一下nextNode1和nextNode2对应的name是否一致
//                                        if (node3.name == node4.name) {
//                                            //  删除以上4个node，注意：InsnList是双向链表，在删除过程中，要保证链表不能断
////                                            while (insnNode.next != node4) {
////
////                                            }
//                                            deleteList.add(node1)
//                                            deleteList.add(node2)
//                                            deleteList.add(node3)
//                                            deleteList.add(node4)
////                                            methodNode.instructions.remove(node1)
////                                            methodNode.instructions.remove(node2)
////                                            methodNode.instructions.remove(node3)
////                                            methodNode.instructions.remove(node4)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    /**
//                     * 不能这样做，链表断了
//                     */
//                    deleteList.forEach {
//                        methodNode.instructions.remove(it)
//                    }
                }
            }

            super.transform(cn)
        }
    }
}