package thread;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTaskTransformCore {

    public static void main(String[] args) {
        String filePath = FileUtils.getFilePath("thread/ThreadTest.class");
        byte[] byteArray = FileUtils.readBytes(filePath);

//        ClassWriter classWriter = modifyClass(byteArray);
//        FileUtils.writeBytes(filePath, classWriter.toByteArray());

        byte[] newBytes = doTransform(byteArray);
        FileUtils.writeBytes(filePath, newBytes);
    }

    private static byte[] doTransform(byte[] originBytes) {
        ClassReader classReader = new ClassReader(originBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ThreadOptimizationClassNode threadOptimizationClassNode = new ThreadOptimizationClassNode(classWriter);
        classReader.accept(threadOptimizationClassNode, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private static ClassWriter modifyClass(byte[] byteArray) {
        ClassReader classReader = new ClassReader(byteArray);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new ClassThreadOptVisitor(Opcodes.ASM9, classWriter,
                "query", "()V");
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return classWriter;
    }
}
