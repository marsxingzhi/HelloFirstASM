package com.mars.asm.time.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File

/**
 * Created by geyan on 2021/6/15
 */
class TimeClassVisitor(classVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM7, classVisitor) {

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
        this.className = name
    }


    /**
     * ClassVisitor的visitMethod只会返回方法的名字，描述符，可见性等，
     * 方法体和方法的注解是看不到的，这个得在MethodVisitor中处理
     */
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("TimeClassVisitor---visitMethod---name = $name, descriptor = $descriptor")
        val vm = super.visitMethod(access, name, descriptor, signature, exceptions)
        return TimeMethodVisitor(className, api, vm, access, name, descriptor, className + File.separator + name)
    }
}