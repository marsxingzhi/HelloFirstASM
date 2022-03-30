package aop;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Created by Mars on 2022/3/11
 */
public class AopTransformCore {

    public static void main(String[] args) {
        String filePath = FileUtils.getFilePath("aop/TestAop.class");
        byte[] byteArray = FileUtils.readBytes(filePath);

        byte[] newBytes = doTransform(byteArray);
        FileUtils.writeBytes(filePath, newBytes);
    }

    private static byte[] doTransform(byte[] originBytes) {
        ClassReader classReader = new ClassReader(originBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//        AopClassNode aopClassNode = new AopClassNode(classWriter);

        TestClassVisitor testClassVisitor = new TestClassVisitor(classWriter);
        classReader.accept(testClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}