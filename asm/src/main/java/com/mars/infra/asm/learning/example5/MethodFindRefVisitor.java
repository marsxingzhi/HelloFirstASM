package com.mars.infra.asm.learning.example5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by JohnnySwordMan on 2022/1/28
 */
public class MethodFindRefVisitor extends ClassVisitor {

    private String methodOwner;
    private String methodName;
    private String methodDesc;

    private String currentMethodOwner;
    private String currentMethodName;
    private String currentMethodDesc;

    private Set<String> set = new HashSet<>();

    public MethodFindRefVisitor(int api, ClassVisitor classVisitor, String methodOwner, String methodName, String methodDesc) {
        super(api, classVisitor);
        this.methodOwner = methodOwner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        currentMethodOwner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        currentMethodName = name;
        currentMethodDesc = descriptor;
        return new MethodFindRefAdaptor(Opcodes.ASM9, mv, methodOwner, methodName, methodDesc, currentMethodOwner, currentMethodName, currentMethodDesc, set);
    }

    @Override
    public void visitEnd() {
        for (String info : set) {
            System.out.println("MethodFindRef info = " + info);
        }
        super.visitEnd();
    }

    private static class MethodFindRefAdaptor extends MethodVisitor {

        private String methodOwner;
        private String methodName;
        private String methodDesc;

        private String currentMethodOwner;
        private String currentMethodName;
        private String currentMethodDesc;

        private Set<String> set;

        public MethodFindRefAdaptor(int api, MethodVisitor methodVisitor, String methodOwner, String methodName, String methodDesc,
                                    String currentMethodOwner, String currentMethodName, String currentMethodDesc, Set<String > set) {
            super(api, methodVisitor);
            this.methodOwner = methodOwner;
            this.methodName = methodName;
            this.methodDesc = methodDesc;

            this.currentMethodOwner = currentMethodOwner;
            this.currentMethodName = currentMethodName;
            this.currentMethodDesc = currentMethodDesc;

            this.set = set;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (methodOwner.equals(owner) && methodName.equals(name) && methodDesc.equals(descriptor)) {
                // 这个方法内部调用了test方法，将该方法记录一下
                String info = String.format("%s.%s%s", currentMethodOwner, currentMethodName, currentMethodDesc);
                set.add(info);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
