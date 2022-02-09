package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 添加HelloFirstAsm类里的objValue字段
 *
 * public Object objValue;
 */
class ClassFieldAddVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val fieldAccess: Int,
    private val fieldName: String,
    private val fieldDesc: String
) : ClassVisitor(api, classVisitor) {

    private var hasField = false

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            hasField = true
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitEnd() {
        if (!hasField) {
            val fv = super.visitField(fieldAccess, fieldName, fieldDesc, null, null)
            fv?.visitEnd()
        }
        super.visitEnd()
    }
}