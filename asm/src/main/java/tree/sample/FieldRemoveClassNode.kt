package tree.sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode
import tree.core.ClassTransformer

/**
 * Created by JohnnySwordMan on 2/11/22
 */
class FieldRemoveClassNode(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val fieldName: String?,
    private val fieldDesc: String?): ClassNode(api) {

    override fun visitEnd() {
        // 自己的逻辑，不要写在这个类中
        val transformer = FieldRemoveTransformer(null, fieldName, fieldDesc)
        transformer.transform(this)

        super.visitEnd()

        // 又将tree api变成core api
        classVisitor?.let { accept(it) }
    }

    private class FieldRemoveTransformer(
        transformer: ClassTransformer?,
        private val fieldName: String?,
        private val fieldDesc:String?): ClassTransformer(transformer) {

        override fun transform(cn: ClassNode?) {
            // 完成自己的逻辑，然后交给下一个

            cn?.fields?.removeIf { node ->
                node.name == fieldName && node.desc == fieldDesc
            }

            super.transform(cn)
        }
    }

}
