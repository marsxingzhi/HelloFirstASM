package com.mars.asm.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by geyan on 2021/6/14
 */
class SimpleClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM7, classVisitor), Opcodes {


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return SimpleMethodVisitor(mv)
    }
}