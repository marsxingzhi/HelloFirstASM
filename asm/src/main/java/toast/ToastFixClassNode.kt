package toast

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import tree.core.MethodTransformer

/**
 * Created by Mars on 3/1/22
 */
class ToastFixClassNode(private val classVisitor: ClassVisitor?) : ClassNode(Opcodes.ASM9) {

    override fun visitEnd() {

        val transformer = ToastFixTransformer(null)
        methods?.forEach { methodNode ->
            transformer.transform(methodNode)
        }

        super.visitEnd()

        classVisitor?.let {
            accept(it)
        }
    }

    private class ToastFixTransformer(transformer: MethodTransformer?) : MethodTransformer(transformer) {

        /**
         * 将方法中的toast.show()替换成ShadowToast.show(toast)
         */
        override fun transform(node: MethodNode?) {
            node?.let { methodNode ->
                methodNode.instructions.asIterable().filterIsInstance(MethodInsnNode::class.java).forEach {
                    if (it.opcode == Opcodes.INVOKEVIRTUAL
                        && it.owner == "toast/Toast"
                        && it.name == "show"
                        && it.desc == "()V") {

                        it.opcode = Opcodes.INVOKESTATIC
                        it.owner = "toast/ShadowToast"
                        it.name = "show"
                        it.desc = "(Ltoast/Toast;)V"
                    }
                }
            }
            super.transform(node)
        }
    }
}