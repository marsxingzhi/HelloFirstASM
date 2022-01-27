package com.mars.infra.asm.learning;

import com.mars.infra.asm.learning.example.HelloWorldDump;
import com.mars.infra.asm.learning.example.HelloWorldDump2;
import com.mars.infra.asm.learning.example.HelloWorldDump3;
import com.mars.infra.asm.learning.example5.MethodFindRefVisitor;
import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/25
 */
public class HelloWorldGenerateCode {

//    public static void main(String[] args) {
//        String relativePath = "generate/_HelloWorld.class";
//        String filePath = FileUtils.getFilePath(relativePath);
//
//        byte[] bytes = new HelloWorldDump2().dump();
//        FileUtils.writeBytes(filePath, bytes);
//    }

    public static void main(String[] args) {
        String relative_path = "com/mars/infra/asm/learning/test/HelloWorld_5.class";
        String filePath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filePath);
        ClassReader classReader = new ClassReader(bytes);
        ClassVisitor classVisitor = new MethodFindRefVisitor(Opcodes.ASM9, null,
                "com/mars/infra/asm/learning/test/HelloWorld_5", "test", "(III)V");
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }
}

