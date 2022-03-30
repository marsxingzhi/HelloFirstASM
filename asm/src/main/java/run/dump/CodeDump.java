package run.dump;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Created by Mars on 2022/3/28
 */
public class CodeDump implements Opcodes {

    public static byte[] dump() {
        ClassNode cn = new ClassNode();
        cn.version = V1_8;
        cn.access = ACC_SUPER;
        cn.name = "run/test/HelloWorld2";
        cn.signature = null;
        cn.superName = "java/lang/Object";

        {
            MethodNode methodNode = new MethodNode(0, "<init>", "()V", null, null);
            cn.methods.add(methodNode);

            InsnList il = methodNode.instructions;
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
            il.add(new InsnNode(RETURN));

            methodNode.maxStack = 1;
            methodNode.maxLocals = 1;
        }

        {
            MethodNode methodNode = new MethodNode(ACC_PUBLIC | ACC_STATIC, "hookInit", "(Ljava/lang/String;Ljava/lang/String;I)Z", null, null);
            cn.methods.add(methodNode);

            InsnList il = methodNode.instructions;

            il.add(new InsnNode(ICONST_3));
            il.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));

            il.add(new InsnNode(DUP));
            il.add(new InsnNode(ICONST_0));
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new InsnNode(AASTORE));

            il.add(new InsnNode(DUP));
            il.add(new InsnNode(ICONST_1));
            il.add(new VarInsnNode(ALOAD, 1));
            il.add(new InsnNode(AASTORE));

            il.add(new InsnNode(DUP));
            il.add(new InsnNode(ICONST_2));
            il.add(new VarInsnNode(ILOAD, 2));
            il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
            il.add(new InsnNode(AASTORE));


            il.add(new MethodInsnNode(INVOKESTATIC, "run/test/ProxyInsnChain", "handle", "([Ljava/lang/Object;)Ljava/lang/Object;", false));

            // 加一个装箱操作
            il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));

//            il.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));


            il.add(new VarInsnNode(ISTORE, 3));
            il.add(new VarInsnNode(ILOAD, 3));
            il.add(new InsnNode(IRETURN));

            methodNode.maxStack = 4;
            methodNode.maxLocals = 4;
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

}
