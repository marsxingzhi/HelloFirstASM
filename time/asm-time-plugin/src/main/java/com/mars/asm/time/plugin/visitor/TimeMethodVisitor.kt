package com.mars.asm.time.plugin.visitor

import com.mars.asm.time.plugin.AsmTimeConstant
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

/**
 * Created by geyan on 2021/4/15
 */
class TimeMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
    methodAbsoluteName: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    var start: Int = 0
    var end: Int = 0
    var isMatch = false
    var mMethodName: String? = null

    init {
        mMethodName = methodAbsoluteName?.replace("/", ".")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("TimeMethodVisitor---visitAnnotation---descriptor = $descriptor")
        if (AsmTimeConstant.TIME_INJECT_ANNOTATION_NAME == descriptor) {
            isMatch = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (!isMatch) {
            return
        }
        invokeStatic(Type.getType("Ljava/lang/System;"), Method("currentTimeMillis", "()J"))
        start = newLocal(Type.LONG_TYPE)
        storeLocal(start)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (!isMatch) {
            return
        }

        // 拦截处理，换成TimeManager
        // com.mars.asm.time.library.TimeManager
        if (interceptor()) {
            getStatic(
                Type.getType("Lcom/mars/asm/time/library/TimeManager;"),
                "INSTANCE",
                Type.getType("Lcom/mars/asm/time/library/TimeManager;")
            )
            visitLdcInsn(mMethodName)
            invokeStatic(Type.getType("Ljava/lang/System;"), Method("currentTimeMillis", "()J"))
//            end = newLocal(Type.LONG_TYPE)
//            storeLocal(end)

//            loadLocal(end)
            loadLocal(start)
            math(SUB, Type.LONG_TYPE)
            invokeVirtual(
                Type.getType("Lcom/mars/asm/time/library/TimeManager;"),
                Method("timeMethod", "(Ljava/lang/String;J)V")
            )
            return
        }

        invokeStatic(Type.getType("Ljava/lang/System;"), Method("currentTimeMillis", "()J"))
        end = newLocal(Type.LONG_TYPE)
        storeLocal(end)

        getStatic(Type.getType("Ljava/lang/System;"), "out", Type.getType("Ljava/io/PrintStream;"))
        newInstance(Type.getType("Ljava/lang/StringBuilder;"))
        dup()
        invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"), Method("<init>", "()V"))

        visitLdcInsn("consume time: ")

        invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
        )

        loadLocal(end)
        loadLocal(start)
        math(SUB, Type.LONG_TYPE)

        invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(J)Ljava/lang/StringBuilder;")
        )

        visitLdcInsn(" ms")
        invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
        )
        invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("toString", "()Ljava/lang/String;")
        )
        invokeVirtual(
            Type.getType("Ljava/io/PrintStream;"),
            Method("println", "(Ljava/lang/String;)V")
        )
    }

    private fun interceptor(): Boolean {
        return true
    }
}