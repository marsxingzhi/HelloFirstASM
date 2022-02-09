package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * Created by JohnnySwordMan on 2/3/22
 *
 * 1. 打印HelloFirstAsm的test方法的入参
 * 即打印方法参数和hashCode返回值
 * public int test(String name, int age, long idCard, Object obj) {
 *      int hashCode = 0;
 *      hashCode += name.hashCode();
 *      hashCode += age;
 *      hashCode += (int) (idCard % Integer.MAX_VALUE);
 *      hashCode += obj.hashCode();
 *      return hashCode;
 * }
 *
 */
class ClassMethodParamPrintVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodAccess: Int,
    private val methodName: String,
    private val methodDesc: String
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
//            mv = MethodParamPrintAdapter(api, mv)
            mv = MethodParamPrintAdvanceAdapter(api, mv, methodAccess, methodName, methodDesc)
        }
        return mv
    }
}

// 优化：可扩展，不管方法参数和个数怎么变，都适用
class MethodParamPrintAdvanceAdapter(
    api: Int,
    methodVisitor: MethodVisitor,
    private val methodAccess: Int,
    private val methodName: String,
    private val methodDesc: String
) : MethodVisitor(api, methodVisitor) {

    override fun visitCode() {
        var isStatic = (methodAccess and Opcodes.ACC_STATIC) != 0  // 判断是否是静态方法
        var slotIdx = if (isStatic) 0 else 1

        val methodType = Type.getMethodType(methodDesc)
        val argumentTypes = methodType.argumentTypes
        argumentTypes?.forEach { type ->
            val sort = type.sort  // 类型
            val size = type.size  // 大小，是占1个slot，还是2个
            val descriptor = type.descriptor  // 即字段描述符

            val opcode = type.getOpcode(Opcodes.ILOAD)
            super.visitVarInsn(opcode, slotIdx)  // 加到操作数栈中

            when(sort) {
                Type.BOOLEAN -> printBoolean()
                Type.CHAR -> printChar()
                Type.SHORT or Type.SHORT or Type.INT -> printInt()
                Type.FLOAT -> printFloat()
                Type.LONG -> printLong()
                Type.DOUBLE -> printDouble()
                Type.OBJECT -> {
                    if ("Ljava/lang/String;" == descriptor) {
                        printString()
                    } else {
                        printObject()
                    }
                }
            }
            slotIdx += size
        }

        super.visitCode()
    }


    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            when (opcode) {
                Opcodes.IRETURN -> {
                    super.visitInsn(Opcodes.DUP)
                    printInt()
                }
                Opcodes.FRETURN -> {
                    super.visitInsn(Opcodes.DUP)
                    printFloat()
                }
                Opcodes.LRETURN -> {
                    super.visitInsn(Opcodes.DUP2)
                    printLong();
                }
                Opcodes.DRETURN -> {
                    super.visitInsn(Opcodes.DUP2)
                    printDouble()
                }
                Opcodes.ARETURN -> {
                    super.visitInsn(Opcodes.DUP)
                    printObject()
                }
                Opcodes.RETURN -> {
                }
            }
        }
        super.visitInsn(opcode)
    }

    private fun printDouble() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.DUP_X2)
        mv.visitInsn(Opcodes.POP)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(D)V",
            false
        )
    }

    private fun printObject() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/Object;)V",
            false
        )
    }

    private fun  printString() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
    }

    private fun printLong() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.DUP_X2)
        mv.visitInsn(Opcodes.POP)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(J)V",
            false
        )
    }

    private fun printFloat() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(F)V",
            false
        )
    }

    private fun printInt() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(I)V",
            false
        )
    }

    private fun printChar() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(C)V",
            false
        )
    }

    private fun printBoolean() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitInsn(Opcodes.SWAP)  // 将栈顶的两个元素交换
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Z)V",
            false
        )
    }
}

/**
 * 注意点：
 * 1. long和double占据两个slot，其余占用1个，因此打印最后一个Object参数时，下标是5，不是4
 * 2. 打印时，描述符要对应
 */
class MethodParamPrintAdapter(api: Int, methodVisitor: MethodVisitor) : MethodVisitor(
    api,
    methodVisitor
) {

    // String name, int age, long idCard, Object obj
    override fun visitCode() {
        // 打印第1个参数 String name
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )

        // 打印第2个参数 int age
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.ILOAD, 2)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)

        //  打印第2个参数 long idCard
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.LLOAD, 3)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false)

        // 打印第3个参数 Object obj
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.ALOAD, 5)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/Object;)V",
            false
        )
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            mv.visitVarInsn(Opcodes.ILOAD, 6)  // 局部变量表上第6个位置的数据，int类型，注意：不是IALOAD
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(I)V",
                false
            )
        }
        super.visitInsn(opcode)
    }
}