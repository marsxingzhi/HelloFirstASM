package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.Printer

/**
 * Created by JohnnySwordMan on 2/5/22
 */
class ClassMethodInvokeFindV2Visitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val invokedMethodName: String,  // 被调用的方法名称
    private val invokedMethodDesc: String
) : ClassVisitor(api, classVisitor) {

    private var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodInvokeFindV2Adapter(api, mv, className, name, descriptor,
            invokedMethodName, invokedMethodDesc)
    }
}

class MethodInvokeFindV2Adapter(
    api: Int, methodVisitor: MethodVisitor,
    private val className: String?,
    private val curMethodName: String?,
    private val curMethodDesc: String?,
    private val invokedMethodName: String,
    private val invokedMethodDesc: String
) : MethodVisitor(api, methodVisitor) {

    private val methodInvokeSet = mutableSetOf<String>()

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (name == invokedMethodName && descriptor == invokedMethodDesc) {
            val info = String.format(
                "%s %s.%s%s", Printer.OPCODES[opcode], className, curMethodName, curMethodDesc)
            methodInvokeSet.add(info)
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitEnd() {
        super.visitEnd()
        methodInvokeSet.forEach { info ->
            println("$invokedMethodName is invoked by $info")
        }
    }
}