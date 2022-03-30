package run;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import run.utils.TreePrinter;

/**
 * Print asm tree code.
 * 打印方法对应的 tree api指令
 */
public class PrintASMCodeTree {
    public static void main(String[] args) throws IOException {
        String className = "run.test.HelloWorld1";
        Printer printer = new TreePrinter();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className)
                .accept(traceClassVisitor, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
    }
}
