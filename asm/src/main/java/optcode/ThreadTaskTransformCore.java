package optcode;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import thread.ClassThreadOptVisitor;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTaskTransformCore {


    public static void main(String[] args) {
        String relative_path = "optcode/HelloFirstAsm.class";
        String filePath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filePath);

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);


        ClassRemoveLogVisitor cv = new ClassRemoveLogVisitor(Opcodes.ASM9, classWriter);

        ClassRemoveLog2Visitor cv1 = new ClassRemoveLog2Visitor(Opcodes.ASM9, cv);

        classReader.accept(cv, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        FileUtils.writeBytes(filePath, classWriter.toByteArray());
    }
}
