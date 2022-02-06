package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import sample.base.MethodPatternAdapter

/**
 * Created by JohnnySwordMan on 2/6/22
 */
class ClassMethodRemovePrintVisitor(
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
        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0
        if (!isAbstractMethod && !isNativeMethod && name == methodName && descriptor == methodDesc) {
            mv = MethodRemovePrintAdapter(api, mv)
        }
        return mv
    }
}

/**
 * 删除System.out.println()语句
 * 该语句设计三个方法，可以定义两个状态
 * mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
 * mv.visitLdcInsn("");
 * mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
 *
 */
class MethodRemovePrintAdapter(api: Int, methodVisitor: MethodVisitor): MethodPatternAdapter(api, methodVisitor) {

    private val SEEK_GETSTATIC = 1
    private val SEEK_LDC = 2

    private var msg: Any? = null


    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        val value = (opcode == Opcodes.GETSTATIC) && (owner == "java/lang/System") && (name == "out") && (descriptor == "Ljava/io/PrintStream;")
        when(state) {
            SEEK_NOTHING -> {
                if (value) {
                    state = SEEK_GETSTATIC
                    return
                }
//                if (opcode == Opcodes.GETSTATIC) {
//                    state = SEEK_GETSTATIC
//                    return
//                }
            }
            SEEK_GETSTATIC -> {  // 连续出现两个System.out，那么先传递一个指令
                if (value) {
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                    return
                }
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }

    override fun visitLdcInsn(value: Any?) {
        when(state) {
            SEEK_GETSTATIC -> {
                state = SEEK_LDC
                msg = value
                return
            }
            SEEK_LDC -> {
                mv.visitLdcInsn(value)
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
        val value = ((opcode == Opcodes.INVOKEVIRTUAL)
                && (owner == "java/io/PrintStream")
                && (name == "println")
                && (descriptor == "(Ljava/lang/String;)V"))
                && !isInterface
        when(state) {
            SEEK_LDC -> {
                if (value) {
                    state = SEEK_NOTHING
                    return
                }
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitInsn() {
        when(state) {
            SEEK_GETSTATIC -> {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/io/PrintStream", "out", "Ljava/io/PrintStream;")
            }
            SEEK_LDC -> {  // 此时还处于该状态，那么需要将两个指令都传递到后面
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/io/PrintStream", "out", "Ljava/io/PrintStream;")
                mv.visitLdcInsn(msg)
            }
        }
        state = SEEK_NOTHING
    }

}