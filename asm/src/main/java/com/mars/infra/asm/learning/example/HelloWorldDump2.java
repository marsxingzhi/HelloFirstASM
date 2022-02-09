package com.mars.infra.asm.learning.example;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/25
 */
public class HelloWorldDump2 implements Opcodes {

    /**
     * public interface HelloWorld extends Cloneable {
     *     @Tag(name = "张三", age = 21)
     *     int less = -1;
     *     int equal = 0;
     *     int greater = 1;
     *     int compareTo(Object obj);
     * }
     */
    public byte[] dump() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(V1_8,
                ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                "generate/_HelloWorld",
                null,
                "java/lang/Object",
                new String[]{"java/lang/Cloneable"});

        {
            FieldVisitor fieldVisitor = classWriter.visitField(
                    ACC_PUBLIC + ACC_STATIC + ACC_FINAL,
                    "less",
                    "I",
                    null,
                    -1);

            {
                AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Lcom/mars/infra/asm/learning/example/Tag", false);
                annotationVisitor.visit("name", "张三");
                annotationVisitor.visit("age", 21);
                annotationVisitor.visitEnd();
            }

            fieldVisitor.visitEnd();
        }

        {
            FieldVisitor fieldVisitor = classWriter.visitField(
                    ACC_PUBLIC + ACC_STATIC + ACC_FINAL,
                    "equal",
                    "I",
                    null,
                    0);
            fieldVisitor.visitEnd();
        }

        {
            FieldVisitor fieldVisitor = classWriter.visitField(
                    ACC_PUBLIC + ACC_STATIC + ACC_FINAL,
                    "greater",
                    "I",
                    null,
                    1);
            fieldVisitor.visitEnd();
        }

        {
            MethodVisitor methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC + ACC_ABSTRACT,
                    "compareTo",
                    "(Ljava/lang/Object;)I",
                    null,
                    null);
            methodVisitor.visitEnd();
        }

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
