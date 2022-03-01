package toast;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Created by Mars on 3/1/22
 */
public class ToastTaskTransformCode {

    public static void main(String[] args) {
        String filePath = FileUtils.getFilePath("toast/ToastTest.class");
        byte[] byteArray = FileUtils.readBytes(filePath);

        byte[] newBytes = doTransform(byteArray);
        FileUtils.writeBytes(filePath, newBytes);
    }

    private static byte[] doTransform(byte[] originBytes) {
        ClassReader classReader = new ClassReader(originBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ToastFixClassNode toastFixClassNode = new ToastFixClassNode(classWriter);

        classReader.accept(toastFixClassNode, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}
