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

        ClassVisitor cv1 = new ClassChangeVisitor(Opcodes.ASM9, classWriter);

        ClassVisitor cv2 = new ClassFieldRemoveVisitor(Opcodes.ASM9, classWriter,
                "strValue", "Ljava/lang/String;");

        ClassFieldAddVisitor cv3 = new ClassFieldAddVisitor(Opcodes.ASM9, classWriter,
                Opcodes.ACC_PUBLIC , "objValue", "Ljava/lang/Object;");

        ClassMethodRemoveVisitor cv4 = new ClassMethodRemoveVisitor(Opcodes.ASM9, classWriter,
                "add", "(II)I");

        ClassMethodAddVisitor cv5 = new ClassMethodAddVisitor(Opcodes.ASM9, classWriter,
                Opcodes.ACC_PUBLIC, "mul", "(II)I");

        MethodEnterExitVisitor cv6 = new MethodEnterExitVisitor(Opcodes.ASM9, classWriter,
                "test", "()V");

        ClassMethodParamPrintVisitor cv7 = new ClassMethodParamPrintVisitor(Opcodes.ASM9, classWriter,
                Opcodes.ACC_PUBLIC,"test", "(Ljava/lang/String;IJLjava/lang/Object;)I");

        ClassMethodTimeVisitor cv8 = new ClassMethodTimeVisitor(Opcodes.ASM9, classWriter, "add2");

        ClassClearMethodVisitor cv9 =
                new ClassClearMethodVisitor(Opcodes.ASM9, classWriter, "verify", "(Ljava/lang/String;Ljava/lang/String;)V");

        ClassMethodInvokeReplaceVisitor cv10 = new ClassMethodInvokeReplaceVisitor(Opcodes.ASM9, classWriter,
                "test", "(II)V");

        ClassMethodInvokeFindVisitor cv11 = new ClassMethodInvokeFindVisitor(Opcodes.ASM9, classWriter,
                "test1", "(II)V");

        ClassMethodInvokeFindV2Visitor cv12 = new ClassMethodInvokeFindV2Visitor(Opcodes.ASM9, classWriter,
                "test3", "(III)V");

        ClassMethodOptVisitor cv13 = new ClassMethodOptVisitor(Opcodes.ASM9, classWriter,
                "test4", "(II)V");

        ClassMethodRemovePrintVisitor cv14 = new ClassMethodRemovePrintVisitor(Opcodes.ASM9, classWriter,
                "test5", "(II)V");

        ClassMethodRemoveInsnVisitor cv15 = new ClassMethodRemoveInsnVisitor(Opcodes.ASM9, classWriter,
                "test6", "(II)V");

        classReader.accept(cv15, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        FileUtils.writeBytes(filePath, classWriter.toByteArray());
    }
}
