package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * Created by JohnnySwordMan on 2/4/22
 */
class ClassClearMethodVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String): ClassVisitor(api, classVisitor) {

    /**
     * 清空方法体，方法名字@methodName，方法描述符是@methodDesc
     * how:
     * 1. MethodVisitor返回null
     * 2. 构造一条空方法
     */
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        if (name == methodName && descriptor == methodDesc) {
            buildEmptyBody2(access, name, descriptor, signature, exceptions)
            return null
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    // 存在问题：返回值固定了
    private fun buildEmptyBody(access: Int, signature: String?, exceptions: Array<out String>?) {
        val mv = super.visitMethod(access, methodName, methodDesc, signature, exceptions)
        mv.visitCode()
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 3)  // 局部变量表大小为3，内容分别为 this、username、password
        mv.visitEnd()
    }

    /**
     * 通用方法：不管方法参数是什么、个数是多少，返回值是什么，都可适用
     */
    private fun buildEmptyBody2(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?) {

        val isStatic = (access and Opcodes.ACC_STATIC) != 0

        val methodType = Type.getMethodType(descriptor)
        val argumentTypes = methodType.argumentTypes
        val returnType = methodType.returnType

        var localSize = if (isStatic) 0 else 1
        argumentTypes.forEach {
            localSize += it.size
        }

        val stackSize =  returnType.size

        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        mv.visitCode()
        if (returnType.sort == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN)
        } else if (returnType.sort >= Type.BOOLEAN && returnType.sort <= Type.DOUBLE) {
            mv.visitInsn(returnType.getOpcode(Opcodes.ICONST_0))  // 空方法的默认返回值
            mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN))
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL)
            mv.visitInsn(Opcodes.ARETURN)
        }

        mv.visitMaxs(stackSize, localSize)
        mv.visitEnd()
    }
}