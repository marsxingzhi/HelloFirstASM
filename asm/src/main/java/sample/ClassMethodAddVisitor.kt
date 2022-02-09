package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 添加HelloFirstAsm类中的mul方法
 *
 * public int mul(int a, int b) {
 *      return a * b;
 * }
 */
class ClassMethodAddVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodAccess: Int,
    private val methodName: String,
    private val methodDesc: String
) : ClassVisitor(api, classVisitor) {

    private var hasMethod = false

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            hasMethod = true
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        if (!hasMethod) {
            val mv = super.visitMethod(methodAccess, methodName, methodDesc, null, null)
            // 生成方法体
            mv?.let {
                generateMethodBody(it)
            }
        }
        super.visitEnd()
    }

    private fun generateMethodBody(methodVisitor: MethodVisitor) {
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 2)
        methodVisitor.visitInsn(Opcodes.IMUL)
        methodVisitor.visitInsn(Opcodes.IRETURN)
        methodVisitor.visitMaxs(2, 3)
        methodVisitor.visitEnd()
    }
}