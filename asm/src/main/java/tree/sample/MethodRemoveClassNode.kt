package tree.sample


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode
import tree.core.ClassTransformer

/**
 * Created by JohnnySwordMan on 2/11/22
 */
class MethodRemoveClassNode(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val removeMethodName: String?,
    private val removeMethodDesc: String?): ClassNode(api) {

    override fun visitEnd() {
        val transformer = RemoveTransformer(null, removeMethodName, removeMethodDesc)
        transformer.transform(this)

        super.visitEnd()

        classVisitor?.let { accept(it) }
    }

    private class RemoveTransformer(
        transformer: ClassTransformer?,
        private val methodRemoveName: String?,
        private val methodRemoveDesc: String?): ClassTransformer(transformer) {

        override fun transform(cn: ClassNode?) {

            cn?.methods?.removeIf { node ->
                node.name == methodRemoveName && node.desc == methodRemoveDesc
            }

            super.transform(cn)
        }

    }

}