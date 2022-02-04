# README    



### TODO   

- [x] 简单demo，插入println方法，详见demo:asm-plugin
- [ ] 计算方法耗时的gradle plugin
- [ ] 仿照Hunter，将它的demo都实现一遍
- [ ] 修改-替换方法调用
- [ ] 查找-方法调用
- [ ] 删除-清空方法体
- [ ] 删除-移除Instruction
- [ ] 添加-方法进入和退出


### 方法耗时gradle plugin      

- [x] 方法耗时，简单实现
- [ ] 新增Extension，添加属性enable，是否开启方法耗时插入
- [ ] 整理，好好整理，就目前阶段，不要求完全看懂字节码，或者说熟练写出字节码，但是需要知道字节码的基本知识

### Label    
Label的使用可以分成三个步骤：
1. 创建Label对象
2. 确定Label位置，`mv.visitLabel`
3. 使用跳转方法，建立联系，例如：`mv.visitJumpInsn`

## 示例   
### 一、修改类信息    
修改类的编译版本、类名以及实现Cloneable接口
```Kotlin
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
```   
### 二、删除类字段     
```Kotlin
/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 删除HelloFirstAsm类里的strValue字段
 *
 * public String strValue;
 */
class ClassFieldRemoveVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val fieldName: String,
    private val fieldDesc: String
) : ClassVisitor(api, classVisitor) {

    /**
     * ClassReader：输入流
     * ClassWriter：输出流
     * ClassVisitor：管道
     * 要想删除某个字段或方法，在对应的visitField或者visitMethod方法中返回null，
     * 那么就没有输出了，即在class文件中的表现就是字段被删除了
     */
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor? {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            return null
        }
        return super.visitField(access, name, descriptor, signature, value)
    }
}
```    
### 三、添加类字段      
```Kotlin
/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 添加HelloFirstAsm类里的objValue字段
 *
 * public Object objValue;
 */
class ClassFieldAddVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val fieldAccess: Int,
    private val fieldName: String,
    private val fieldDesc: String
) : ClassVisitor(api, classVisitor) {

    private var hasField = false

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        if (name.equals(fieldName) && descriptor.equals(fieldDesc)) {
            hasField = true
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitEnd() {
        if (!hasField) {
            val fv = super.visitField(fieldAccess, fieldName, fieldDesc, null, null)
            fv?.visitEnd()
        }
        super.visitEnd()
    }
}
```      
### 四、删除类方法 
```Kotlin
/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 删除HelloFirstAsm类中的add方法
 *
 * public int add(int a, int b) {
 *      return a - b;
 * }
 */
class ClassMethodRemoveVisitor(
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
    ): MethodVisitor? {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            return null
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}
```     
### 五、添加类方法     
```Kotlin
/**
 * Created by JohnnySwordMan on 2/2/22
 *
 * 1. 添加HelloFirstAsm类中的mul方法
 *
 * public int mul(int a, int b) {
 *      return a * b;
 * }
 */
class ClassMethodAddVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val methodAccess: Int,
    private val methodName: String,
    private val methodDesc: String
) : ClassVisitor(api, classVisitor) {

    private var hasMethod = false

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            hasMethod = true
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        if (!hasMethod) {
            val mv = super.visitMethod(methodAccess, methodName, methodDesc, null, null)
            // 生成方法体
            mv?.let {
                generateMethodBody(it)
            }
        }
        super.visitEnd()
    }

    private fun generateMethodBody(methodVisitor: MethodVisitor) {
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 2)
        methodVisitor.visitInsn(Opcodes.IMUL)
        methodVisitor.visitInsn(Opcodes.IRETURN)
        methodVisitor.visitMaxs(2, 3)
        methodVisitor.visitEnd()
    }
}
```
### 六、修改方法-添加-进入和退出
在方法开始和结束的时候，添加打印语句
```Kotlin
internal class EnterExitAdapter(api: Int, methodVisitor: MethodVisitor): MethodVisitor(api, methodVisitor) {

    override fun visitCode() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("method start")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            mv.visitLdcInsn("method end")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        }
        super.visitInsn(opcode)
    }

    /**
     * 虽然这个方法是最后调用的，标志方法体的结束，但是方法其实在这个方法执行之前都已经完成了。
     * 准确的讲，在执行return语句的时候，方法已经完成了，所以在visitMaxs方法中添加是不生效的
     */
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack, maxLocals)
    }
}
```   
### 七、修改方法-添加-打印方法参数和返回值    
```Kotlin
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
            mv = MethodParamPrintAdapter(api, mv)
        }
        return mv
    }
}

/**
 * 注意点：
 * 1. long和double占据两个slot，其余占用1个，因此打印最后一个Object参数时，下标是5，不是4
 * 2. 打印时，描述符要对应
 */
class MethodParamPrintAdapter(api: Int, methodVisitor: MethodVisitor): MethodVisitor(api, methodVisitor) {

    // String name, int age, long idCard, Object obj
    override fun visitCode() {
        // 打印第1个参数 String name
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)

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
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false)
        super.visitCode()
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            mv.visitVarInsn(Opcodes.ILOAD, 6)  // 局部变量表上第6个位置的数据，int类型，注意：不是IALOAD
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)
        }
        super.visitInsn(opcode)
    }
}
```   
优化：可以打印任意方法参数和返回值
```Kotlin
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
```   
### 八、方法耗时     
- 定义timer字段，timer值表示该方法的耗时
- 在方法开始时，添加`timer -= System.currentTimeMillis()`
- 在方法结束时，添加`timer += System.currentTimeMillis()`

详细代码可参考：`ComputeTimeAdapter`

### 九、清空方法体     
- 对应的MethodVisitor返回null
- 构造一个方法名和描述符相同的空方法   

详细代码可参考：`ClassClearMethodVisitor`

### 十、替换方法调用   
- 替换Instruction前后，需要保证操作数栈一致   

详细代码可参考：`ClassMethodInvokeReplaceVisitor`

### Thread-Task  
线程替换，将系统线程替换成自定义的线程。
- [ ] new Thread