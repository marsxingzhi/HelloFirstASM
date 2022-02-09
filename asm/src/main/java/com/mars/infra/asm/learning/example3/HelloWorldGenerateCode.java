package com.mars.infra.asm.learning.example3;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/26
 */
public class HelloWorldGenerateCode {

    public static void main(String[] args) {
        String relative_path = "com/mars/infra/asm/learning/test/HelloWorld_3.class";
        String filePath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filePath);

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new ClassChangeVisitor(Opcodes.ASM9, classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        // 将修改后的class写入
        FileUtils.writeBytes(filePath, classWriter.toByteArray());
    }
}