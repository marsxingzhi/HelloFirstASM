package com.mars.infra.asm.learning.thread;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTaskMain {

    /**
     * 修改ThreadTest中的Thread创建，替换成自定义的
     */
    public static void main(String[] args) {
        String relative_path = "com/mars/infra/asm/learning/thread/ThreadTest.class";
        String filePath = FileUtils.getFilePath(relative_path);
        byte[] byteArray = FileUtils.readBytes(filePath);
        ClassWriter classWriter = modifyClass(byteArray);
        FileUtils.writeBytes(filePath, classWriter.toByteArray());
    }

    private static ClassWriter modifyClass(byte[] byteArray) {
        ClassReader classReader = new ClassReader(byteArray);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new ThreadClassVisitor(Opcodes.ASM9, classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return classWriter;
    }
}
