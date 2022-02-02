package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 修改HelloFirstAsm类的版本，原先是Java8编译的，现在改成Java11
 * 2. 修改HelloFirstAsm类的接口，实现Cloneable接口
 */
class ClassChangeVisitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(Opcodes.V11, access, "${name}_v2", signature, superName, arrayOf("java/lang/Cloneable"))
    }
}