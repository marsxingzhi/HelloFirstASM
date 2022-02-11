package tree.sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import tree.core.ClassTransformer

/**
 * Created by JohnnySwordMan on 2/11/22
 */
class FieldAddClassNode(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val addFieldName: String?,
    private val addFieldDesc: String?
) : ClassNode(api) {

    override fun visitEnd() {
        // 自己逻辑
        FieldAddTransformer(null, addFieldName, addFieldDesc).transform(this)

        super.visitEnd()

        classVisitor?.let { accept(it) }
    }

    private class FieldAddTransformer(
        transformer: ClassTransformer?,
        private val addFieldName: String?,
        private val addFieldDesc: String?) : ClassTransformer(transformer), Opcodes {

        override fun transform(cn: ClassNode?) {

            val objFieldNode = FieldNode(ACC_PRIVATE, addFieldName, addFieldDesc, null, null)

            // 这个可以选择加到哪个位置
//        cn?.fields?.add(0, objFieldNode)

            // 添加字段之前需要判断字段是否存在
            var hasPresent = false
            cn?.fields?.forEach { fn ->
                if (fn.name == addFieldName && fn.desc == addFieldDesc) {
                    hasPresent = true
                }
            }
            if (!hasPresent) {
                cn?.fields?.add(objFieldNode)
            }

            super.transform(cn)
        }
    }
}