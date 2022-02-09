package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.Printer

/**
 * Created by JohnnySwordMan on 2/5/22
 */
class ClassMethodInvokeFindVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String
): ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == methodName && descriptor == methodDesc) {
            mv = MethodInvokeFindAdapter(api, mv)
        }
        return mv
    }
}

class MethodInvokeFindAdapter(api: Int, methodVisitor: MethodVisitor): MethodVisitor(api, methodVisitor) {

    private val methodInvokeSet = mutableSetOf<String>()

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        val info = String.format(
            "%s %s.%s%s", Printer.OPCODES[opcode], owner, name, descriptor)
        methodInvokeSet.add(info)
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitEnd() {
        super.visitEnd()
        println("test1方法中调用了如下方法：")
        methodInvokeSet.forEach { str ->
            println(str)
        }
    }
}