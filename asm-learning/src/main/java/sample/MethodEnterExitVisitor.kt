package sample

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 在方法开始和结束的地方添加一句打印语句
 *
 * public void test() {
 *      System.out.println("this is a test method.");
 * }
 *
 */
class MethodEnterExitVisitor(
    api: Int,
    classVisitor: ClassVisitor,
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
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            return EnterExitAdapter(api, mv)
        }
        return mv
    }
}