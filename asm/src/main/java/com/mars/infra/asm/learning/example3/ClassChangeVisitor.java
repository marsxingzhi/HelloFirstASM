package com.mars.infra.asm.learning.example3;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created by JohnnySwordMan on 2022/1/26
 * 修改内容：
 * 版本
 * 类名
 * 新增接口
 * 删除strValue字段
 * 添加字段
 * 删除方法
 * 添加方法
 */
public class ClassChangeVisitor extends ClassVisitor {

    private boolean hasObjValueField;
    private boolean hasMulMethod;
    private String owner;

    public ClassChangeVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(Opcodes.V1_7, access, name+"_V2", signature, superName, new String[]{"java/lang/Cloneable"});
        owner = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // 先判断objValue字段是否存在
        if (name.equals("objValue")) {
            hasObjValueField = true;
        }
        // 如果想要删除某个字段，返回null即可
        if (name.equals("strValue") && descriptor.equals("Ljava/lang/String;")) {
            return null;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    /**
     * 添加字段：
     * 1. 是在ClassVisitor的visitEnd中操作的，如果在visitField中操作，有可能会重复添加字段，这样就不符合ClassFile定义
     *
     */
    @Override
    public void visitEnd() {
        // 在class中添加字段，肯定是在ClassVisitor中操作
        if (!hasObjValueField) {
            FieldVisitor fieldVisitor = super.visitField(Opcodes.ACC_PRIVATE, "objValue", "Ljava/lang/Object;", null, null);
            if (fieldVisitor != null) {
                fieldVisitor.visitEnd();  // 不要忘记
            }
        }
        if (!hasMulMethod) {
            MethodVisitor methodVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "mul", "(II)I", null, null);
            if (methodVisitor != null) {
                createMulMethodBody(methodVisitor);
            }
        }
        super.visitEnd();
    }

    /**
     * 方法体内部逻辑处理
     * visitCode开始
     * visitEnd结束
     */
    private void createMulMethodBody(MethodVisitor methodVisitor) {
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 2);
        methodVisitor.visitInsn(Opcodes.IMUL);
        methodVisitor.visitInsn(Opcodes.IRETURN);
        methodVisitor.visitMaxs(2, 3);
        methodVisitor.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals("mul") && descriptor.equals("(II)I")) {
            hasMulMethod = true;
        }
        if (name.equals("add") && descriptor.equals("(II)I")) {
            return null;
        }
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        // 也可以对所有方法插入，但要排除构造函数
        if (name.equals("test") && descriptor.equals("()V")) {
            mv = new MethodEnterExitAdapter(Opcodes.ASM9, mv);
        }
        // String name, int age, long idCard, Object obj
        if (name.equals("test1") && descriptor.equals("(Ljava/lang/String;IJLjava/lang/Object;)I")) {
            mv = new MethodChangeVisitor2(Opcodes.ASM9, mv);
        }
        if (name.equals("verify") && descriptor.equals("(Ljava/lang/String;Ljava/lang/String;)V")) {
            // 此时需要生成新的方法，方法名、描述符、返回值都一样，但是是空方法（空方法也有return）
            // owner表示哪个类
            generateEmptyMethodBody(mv, owner, access, name, descriptor);
            return null;  // 返回null，那么原始方法就被擦除了
        }
        if (name.equals("test") && descriptor.equals("(II)V")) {
            mv = new MethodReplaceAdapter(Opcodes.ASM9, mv);
        }
        return mv;
    }

    /**
     * 生成owner类的空方法，方法名是name
     */
    private void generateEmptyMethodBody(MethodVisitor mv, String owner, int access, String name, String descriptor) {
        Type type = Type.getType(descriptor);
        Type[] argumentTypes = type.getArgumentTypes();
        Type returnType = type.getReturnType();

        // 计算局部变量表和栈的大小
        boolean isStaticMethod = (access & Opcodes.ACC_STATIC) != 0;
        int localSize = isStaticMethod ? 0 : 1;  // 如果是静态方法，表中没有数据，如果是实例方法，那么索引0的位置上是this，即表中已经有了1个数据
        for (Type argType: argumentTypes) {
            localSize += argType.getSize();
        }
        int stackSize = returnType.getSize();

        // 方法体
        mv.visitCode();

        if (returnType.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
        } else if (returnType.getSort() >= Type.BOOLEAN && returnType.getSort() <= Type.DOUBLE){
            mv.visitInsn(returnType.getOpcode(Opcodes.ICONST_1));
            mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
        }

        mv.visitMaxs(stackSize, localSize);
        mv.visitEnd();
    }
}