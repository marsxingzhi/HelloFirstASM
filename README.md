# README    



### TODO   

- [x] 简单demo，插入println方法，详见demo:asm-plugin
- [ ] 计算方法耗时的gradle plugin
- [ ] 仿照Hunter，将它的demo都实现一遍


### 方法耗时gradle plugin      

- [x] 方法耗时，简单实现
- [ ] 新增Extension，添加属性enable，是否开启方法耗时插入
- [ ] 整理，好好整理，就目前阶段，不要求完全看懂字节码，或者说熟练写出字节码，但是需要知道字节码的基本知识

### Label    
Label的使用可以分成三个步骤：
1. 创建Label对象
2. 确定Label位置，`mv.visitLabel`
3. 使用跳转方法，建立联系，例如：`mv.visitJumpInsn`

### 修改已有方法
#### 添加-进入和退出
在方法开始和结束的时候，添加打印语句
```Java
public class MethodEnterExitAdapter extends MethodVisitor {


    public MethodEnterExitAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    /**
     * 标志方法体的开始
     * 先处理插入代码，再执行父类方法
     */
    @Override
    public void visitCode() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("开始了！☺️");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("结束！☺️");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }

    /**
     * 虽然这个方法是最后调用的，标志方法体的结束，但是方法其实在这个方法执行之前都已经完成了。
     * 准确的讲，在执行return语句的时候，方法已经完成了，所以在visitMaxs方法中添加是不生效的
     */
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }
}

```

### Thread-Task  
线程替换，将系统线程替换成自定义的线程。
- [ ] new Thread