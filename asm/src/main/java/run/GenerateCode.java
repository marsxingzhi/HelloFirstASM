package run;

import com.mars.infra.asm.learning.example.HelloWorldDump4;
import com.mars.infra.asm.learning.utils.FileUtils;

import run.dump.CodeDump;

/**
 * Created by Mars on 2022/3/28
 * <p>
 * 代码生成
 */
class GenerateCode {

    public static void main(String[] args) {
        String relativePath = "run/test/HelloWorld2.class";
        String filePath = FileUtils.getFilePath(relativePath);

        byte[] bytes = CodeDump.dump();
        FileUtils.writeBytes(filePath, bytes);
    }
}
