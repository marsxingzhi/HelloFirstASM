package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import sample.base.MethodPatternAdapter

/**
 * Created by JohnnySwordMan on 2/6/22
 */
class ClassMethodOptVisitor(
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
            mv = MethodRemoveAddZeroAdapter(api, mv)
        }
        return mv
    }
}

/**
 * int d = c + 0；目标：去掉+0
 * 加0的指令如下：
 * iconst_0
 * iadd
 *
 * 以上两个指令涉及到的方法是visitInsn(int opcodes)，因此直接在该方法内修改
 */
class MethodRemoveAddZeroAdapter(api: Int, methodVisitor: MethodVisitor): MethodPatternAdapter(api, methodVisitor) {

    companion object {
        private const val SEEK_ICONST_0 = 1
    }

    /**
     * 如果是state是SEEK_NOTHING状态，此时opcode是ICONST_0指令，那么将state改成SEEK_ICONST_0状态，暂时不向后传递指令
     * 如果state是SEEK_ICONST_0状态，此时opcode是IADD指令，（ICONST_0、IADD指令前后出现，说明是加0），此时，将state改成初始状态，不向后传递指令
     * 如果state是SEEK_ICONST_0状态，此时opcodes是ICONST_0指令，说明连续出现了两个ICONST_0指令，因此，先向后传递一个
     */
    override fun visitInsn(opcode: Int) {
        when (state) {
            SEEK_NOTHING ->  {
                if (opcode == Opcodes.ICONST_0)   {
                    state = SEEK_ICONST_0
                    return
                }
            }
            SEEK_ICONST_0 -> {
                if (opcode == Opcodes.IADD) {
                    state = SEEK_NOTHING
                    return
                } else if (opcode == Opcodes.ICONST_0) {
                    mv.visitInsn(opcode)
                    return
                }
            }
        }
        super.visitInsn(opcode)
    }

    /**
     * 还原状态
     * 如果state是SEEK_ICONST_0状态，表示之前连续出现了两个ICONST_0，在前面已经传递了一个，这里再向后传递一个；
     * 如果state不是SEEK_ICONST_0状态，则重置state状态
     */
    override fun visitInsn() {
        if (state == SEEK_ICONST_0) {
            mv.visitInsn(Opcodes.ICONST_0)
        }
        state = SEEK_NOTHING
    }

}