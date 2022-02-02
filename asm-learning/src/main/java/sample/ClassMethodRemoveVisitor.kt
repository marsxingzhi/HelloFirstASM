package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 删除HelloFirstAsm类中的add方法
 *
 * public int add(int a, int b) {
 *      return a - b;
 * }
 */
class ClassMethodRemoveVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            return null
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}