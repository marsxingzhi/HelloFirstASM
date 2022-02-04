package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2/4/22
 *
 * 方法替换时，需要注意替换Instruction前后，操作数栈应该保持一致
 */
class ClassMethodInvokeReplaceVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String): ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv =  super.visitMethod(access, name, descriptor, signature, exceptions)
        if (mv != null
            && checkMethodValid(access, name)
            && name == methodName
            && descriptor == methodDesc) {
            mv = ReplaceMethodInvokeAdapter(api, mv)
        }
        return mv
    }

    // 排除构造方法、静态代码块、抽象方法、native方法
    private fun checkMethodValid(access: Int, methodName: String?) : Boolean {
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0
        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        return methodName != "<init>" && methodName != "<clinit>" && !isNativeMethod && !isAbstractMethod
    }
}

class ReplaceMethodInvokeAdapter(api: Int, methodVisitor: MethodVisitor) : MethodVisitor(api, methodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (owner == "java/lang/Math" && name == "max" && descriptor == "(II)I") {
            super.visitMethodInsn(opcode, owner, "min", descriptor, isInterface)
        } else if (owner == "java/io/PrintStream" && name == "println" && descriptor == "(I)V") {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mars/infra/asm/learning/utils/ParameterUtils", "output", "(Ljava/io/PrintStream;I)V", isInterface)
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }
}