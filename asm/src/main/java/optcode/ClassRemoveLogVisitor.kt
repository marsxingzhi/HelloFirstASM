package optcode
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import sample.base.MethodPatternAdapter

/**
 * Created by JohnnySwordMan on 2/8/22
 */
class ClassRemoveLogVisitor(api: Int, classVisitor: ClassVisitor): ClassVisitor(api, classVisitor) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv =  super.visitMethod(access, name, descriptor, signature, exceptions)
        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0
        if (!isAbstractMethod && !isNativeMethod) {
            mv = RemoveLogAdapter(api, mv)
        }
        return mv
    }
}

class RemoveLogAdapter(api: Int, methodVisitor: MethodVisitor): MethodPatternAdapter(api, methodVisitor) {

    private val SEEK_LDC = 1
    private val SEEK_LDC_LDC = 2
    private var isLdcFromLog = false


    override fun visitInsn() {
        when(state) {
            SEEK_LDC -> {
                mv.visitLdcInsn(ldcValue)
            }
            SEEK_LDC_LDC -> {
                if (!isLdcFromLog) {
                    mv.visitLdcInsn(ldcValue)
                    mv.visitLdcInsn(ldcValue2)
                }
            }
        }
        state = SEEK_NOTHING
    }

    private var ldcValue: Any? = null
    private var ldcValue2: Any? = null

    override fun visitLdcInsn(value: Any?) {
        when(state) {
            SEEK_NOTHING -> {
                state = SEEK_LDC
                ldcValue = value
                return
            }
            SEEK_LDC -> {
                state = SEEK_LDC_LDC
                ldcValue2 = value
                return
            }
        }
        super.visitLdcInsn(value)
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        when(state) {
            SEEK_LDC_LDC -> {
                if (opcode == Opcodes.INVOKESTATIC
                    && owner == "optcode/Log"
                    && (name == "i" || name == "d" || name == "e")
                    && descriptor == "(Ljava/lang/String;Ljava/lang/String;)V") {
                    isLdcFromLog = true
                    return
                }
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

}