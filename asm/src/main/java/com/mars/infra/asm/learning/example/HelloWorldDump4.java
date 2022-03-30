package com.mars.infra.asm.learning.example;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/**
 * Created by Mars on 2022/3/22
 */
public class HelloWorldDump4 implements Opcodes {

    // test
    public static byte[] dump() {
        ClassNode cn = new ClassNode();
        cn.version = V1_8;
        cn.access = ACC_PUBLIC | ACC_SUPER;
        cn.name = "generate/_HelloWorld";
        cn.signature = null;
        cn.superName = "java/lang/Object";

        {
            MethodNode methodNode = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
            cn.methods.add(methodNode);

            InsnList il = methodNode.instructions;
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
            il.add(new InsnNode(RETURN));

            methodNode.maxStack = 1;
            methodNode.maxLocals = 1;
        }

        {
            MethodNode methodNode = new MethodNode(ACC_PRIVATE | ACC_STATIC, "_generate_hookLogW_mixin", "(Ljava/lang/String;Ljava/lang/String;)I", null, null);
            cn.methods.add(methodNode);

            InsnList il = methodNode.instructions;
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new TypeInsnNode(NEW, "java/lang/StringBuilder"));
            il.add(new InsnNode(DUP));


            il.add(new MethodInsnNode(INVOKESTATIC, "run/test/ProxyInsnChain", "proceed", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", false));

            il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false));
            il.add(new VarInsnNode(ALOAD, 1));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            il.add(new LdcInsnNode(" ---> TestMixin hook hookLogW success."));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));


//            il.add(new MethodInsnNode(INVOKESTATIC, "run/test/ProxyInsnChain", "proceed", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", false));


            il.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
            il.add(new InsnNode(IRETURN));

            methodNode.maxStack = 3;
            methodNode.maxLocals = 2;
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
