package tree;

import com.mars.infra.asm.learning.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import tree.sample.ComputeTimerClassNode;
import tree.sample.FieldAddClassNode;
import tree.sample.FieldRemoveClassNode;
import tree.sample.MethodAddClassNode;
import tree.sample.MethodRemoveClassNode;
import tree.sample.RemoveCodeClassNode;

/**
 * Created by JohnnySwordMan on 2/11/22
 */
class HelloFirstAsmTransformTree {

    public static void main(String[] args) {
        String relative_path = "tree/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] originBytes = FileUtils.readBytes(filepath);


        byte[] byteArray = doTransform(originBytes);
        FileUtils.writeBytes(filepath, byteArray);
    }

    private static byte[] doTransform(byte[] originBytes) {
        ClassReader cr = new ClassReader(originBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        FieldRemoveClassNode fieldRemoveClassNode = new FieldRemoveClassNode(Opcodes.ASM9, cw, "_strValue", "Ljava/lang/String;");

        FieldAddClassNode fieldAddClassNode = new FieldAddClassNode(Opcodes.ASM9, cw, "_objValue", "Ljava/lang/Object;");

        MethodRemoveClassNode methodRemoveClassNode = new MethodRemoveClassNode(Opcodes.ASM9, cw, "test", "(IJLjava/lang/Object;)Ljava/lang/String;");

        MethodAddClassNode methodAddClassNode = new MethodAddClassNode(Opcodes.ASM9, cw, "generateMul", "(II)I");

        ComputeTimerClassNode computeTimerClassNode = new ComputeTimerClassNode(Opcodes.ASM9, cw);

        RemoveCodeClassNode removeCodeClassNode = new RemoveCodeClassNode(Opcodes.ASM9, cw, "test", "(II)V");

        cr.accept(removeCodeClassNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        return cw.toByteArray();
    }
}
