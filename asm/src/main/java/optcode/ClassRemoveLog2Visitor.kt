package optcode

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import sample.base.MethodPatternAdapter

/**
 * Created by JohnnySwordMan on 2/8/22
 */
class ClassRemoveLog2Visitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0
        if (!isAbstractMethod && !isNativeMethod) {
            mv = RemoveLog2Adapter(api, mv)
        }
        return mv
    }
}

/**
 * say2方法对应的Instruction如下：
 * {
    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "say2", "(Ljava/lang/String;)V", null, null);
    methodVisitor.visitCode();

    methodVisitor.visitLdcInsn("gy");
    methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
    methodVisitor.visitLdcInsn("this is say method, and msg is ");
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    methodVisitor.visitMethodInsn(INVOKESTATIC, "optcode/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)V", false);

    methodVisitor.visitInsn(RETURN);
    methodVisitor.visitMaxs(3, 2);
    methodVisitor.visitEnd();
    }
 *
 * Log.i("tag", "this is say2 method, and msg is " + msg)就有上述这么多代码；
 * 假设存在多个+msg，那么就有重复的Instrcution，主要是StringBuilder的逻辑，这样就没法写了
 */
class RemoveLog2Adapter(api: Int, methodVisitor: MethodVisitor) : MethodPatternAdapter(api, methodVisitor) {

    private val SEEK_LDC = 1

    override fun visitInsn() {

    }

    /**
     * 通过判断是否调用Log.i方法，如果调用，就不向后传递，也可以做到消除Log.i语句，但是其他的Instrcution还是存在，
     * 有一个原则：不管怎么处理，但是要保证前后的操作数栈一致
     * 通过这种方式，很明显是不一样的
     */
    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (opcode == Opcodes.INVOKESTATIC && owner == "optcode/Log" && name == "i" && descriptor == "(Ljava/lang/String;Ljava/lang/String;)V") {

        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }

}