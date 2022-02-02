package sample;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Created by JohnnySwordMan on 2022/2/1
 */
class HelloFirstTransformCore {

    public static void main(String[] args) {
        String relative_path = "sample/HelloFirstAsm.class";
        String filePath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filePath);

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

//        ClassVisitor classVisitor = new ClassChangeVisitor(Opcodes.ASM9, classWriter);
//        ClassVisitor classVisitor = new ClassFieldRemoveVisitor(Opcodes.ASM9, classWriter,
//                "strValue", "Ljava/lang/String;");

//        ClassFieldAddVisitor classVisitor = new ClassFieldAddVisitor(Opcodes.ASM9, classWriter,
//                Opcodes.ACC_PUBLIC , "objValue", "Ljava/lang/Object;");

//        ClassMethodRemoveVisitor classVisitor = new ClassMethodRemoveVisitor(Opcodes.ASM9, classWriter,
//                "add", "(II)I");
        ClassMethodAddVisitor classVisitor = new ClassMethodAddVisitor(Opcodes.ASM9, classWriter,
                Opcodes.ACC_PUBLIC, "mul", "(II)I");


        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        FileUtils.writeBytes(filePath, classWriter.toByteArray());
    }
}
