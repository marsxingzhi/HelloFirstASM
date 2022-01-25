package com.mars.infra.asm.learning;

import com.mars.infra.asm.learning.example.HelloWorldDump;
import com.mars.infra.asm.learning.example.HelloWorldDump2;
import com.mars.infra.asm.learning.example.HelloWorldDump3;
import com.mars.infra.asm.learning.utils.FileUtils;

/**
 * Created by JohnnySwordMan on 2022/1/25
 */
public class HelloWorldGenerateCode {

    public static void main(String[] args) {
        String relativePath = "generate/_HelloWorld.class";
        String filePath = FileUtils.getFilePath(relativePath);

        byte[] bytes = new HelloWorldDump2().dump();
        FileUtils.writeBytes(filePath, bytes);
    }
}

