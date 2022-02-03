package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/**
 * Created by JohnnySwordMan on 2/3/22
 */
class ClassMethodTimeVisitor(api: Int, classVisitor: ClassVisitor, private val methodName: String) :
    ClassVisitor(api, classVisitor) {

    private var isInterface = false
    private var owner: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        isInterface = (access and Opcodes.ACC_INTERFACE) != 0
        owner = name
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (!isInterface && name.equals(methodName)) {
            mv = ComputeTimeAdapter(api, mv, owner!!)
        }
        return mv
    }

    override fun visitEnd() {
        if (!isInterface) {
            val fv = super.visitField(Opcodes.ACC_PUBLIC, "timer", "J", null, null)
            fv?.visitEnd()
        }
        super.visitEnd()
    }
}

/**
 * 计算add2方法的耗时，在进入方法的时候执行timer -= System.currentTimeMillis()
 * 在方法结束的时候，执行 timer += System.currentTimeMillis()
 *
 * 最终，timer记录的就是该方法的耗时
 */
class ComputeTimeAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val owner: String
) : MethodVisitor(api, methodVisitor) {

    override fun visitCode() {
        // 需要加载this变量，如果timer是静态的，则不需要
        super.visitVarInsn(Opcodes.ALOAD, 0)
        super.visitInsn(Opcodes.DUP)


        super.visitFieldInsn(Opcodes.GETFIELD, owner, "timer", "J")
        super.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        super.visitInsn(Opcodes.LSUB)
        super.visitFieldInsn(Opcodes.PUTFIELD, owner, "timer", "J")
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitInsn(Opcodes.DUP)


            super.visitFieldInsn(Opcodes.GETFIELD, owner, "timer", "J")
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
            )
            super.visitInsn(Opcodes.LADD)
            super.visitFieldInsn(Opcodes.PUTFIELD, owner, "timer", "J")
        }
        super.visitInsn(opcode)
    }
}