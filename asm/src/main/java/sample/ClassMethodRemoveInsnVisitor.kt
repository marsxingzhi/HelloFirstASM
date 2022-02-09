package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import sample.base.MethodPatternAdapter

/**
 * Created by JohnnySwordMan on 2/7/22
 */
class ClassMethodRemoveInsnVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val methodDesc: String): ClassVisitor(api, classVisitor) {

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

        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        val isStaticMethod = (access and Opcodes.ACC_STATIC) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0
        if (!isStaticMethod && !isNativeMethod && name == methodName && descriptor == methodDesc) {
            mv = RemoveInsAdapter(api, mv, className)
        }
        return mv
    }
}

/**
 * 删除this.val = this.val语句
 * Instruction如下：
 * aload_0
 * aload_0
 * getfield
 * putfield
 *
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitFieldInsn(GETFIELD, "com/mars/infra/asm/learning/test/HelloWorldTest", "val", "I");
methodVisitor.visitFieldInsn(PUTFIELD, "com/mars/infra/asm/learning/test/HelloWorldTest", "val", "I");
 */
class RemoveInsAdapter(api: Int, methodVisitor: MethodVisitor, private val className: String?): MethodPatternAdapter(api, methodVisitor) {

    private val SEEK_ALOAD_0 = 1
    private val SEEK_ALOAD_0_ALOAD_0 = 2
    private val SEEK_GETFIELD = 3

    override fun visitInsn() {
        when(state) {
            SEEK_ALOAD_0 -> {
                mv.visitVarInsn(Opcodes.ALOAD, 0)
            }
            SEEK_ALOAD_0_ALOAD_0 -> {
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
            }
            SEEK_GETFIELD -> {
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(Opcodes.GETFIELD, fieldOwner, fieldName, fieldDesc)
            }
        }
        state = SEEK_NOTHING
    }

    override fun visitVarInsn(opcode: Int, value: Int) {
        when(state) {
            SEEK_NOTHING -> {
                if (opcode == Opcodes.ALOAD && value == 0) {
                    state = SEEK_ALOAD_0
                    return
                }
            }
            SEEK_ALOAD_0 -> {
                if (opcode == Opcodes.ALOAD && value == 0) {
                    state = SEEK_ALOAD_0_ALOAD_0
                    return
                }
            }
            SEEK_ALOAD_0_ALOAD_0 -> {
                if (opcode == Opcodes.ALOAD && value == 0) {
                    // 连续出现3个ALOAD_0
                    mv.visitVarInsn(opcode, value)
                    return
                }
            }
        }
        super.visitVarInsn(opcode, value)
    }

    private var fieldOwner: String? = null
    private var fieldName: String? = null
    private var fieldDesc: String? = null

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        when(state) {
            SEEK_ALOAD_0_ALOAD_0 -> {
                // name == "val"，这样就不能拓展了
//                if (opcode == Opcodes.GETFIELD && owner == className && name == "val" && descriptor == "I") {
//                    state = SEEK_GETFIELD
//                    return
//                }
                if (opcode == Opcodes.GETFIELD) {
                    state = SEEK_GETFIELD
                    fieldOwner = owner
                    fieldName = name
                    fieldDesc = descriptor
                    return
                }
            }
            SEEK_GETFIELD -> {
//                if (opcode == Opcodes.PUTFIELD && owner == className && name == "val" && descriptor == "I") {
//                    state = SEEK_NOTHING
//                    return
//                }
                if (opcode == Opcodes.PUTFIELD && name == fieldName) {
                    // getField和putField操作的name一致
                    state = SEEK_NOTHING
                    return
                }
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }





}