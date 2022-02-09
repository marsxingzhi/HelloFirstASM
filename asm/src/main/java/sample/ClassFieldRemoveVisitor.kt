package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 删除HelloFirstAsm类里的strValue字段
 *
 * public String strValue;
 */
class ClassFieldRemoveVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val fieldName: String,
    private val fieldDesc: String
) : ClassVisitor(api, classVisitor) {

    /**
     * ClassReader：输入流
     * ClassWriter：输出流
     * ClassVisitor：管道
     * 要想删除某个字段或方法，在对应的visitField或者visitMethod方法中返回null，
     * 那么就没有输出了，即在class文件中的表现就是字段被删除了
     */
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor? {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            return null
        }
        return super.visitField(access, name, descriptor, signature, value)
    }
}