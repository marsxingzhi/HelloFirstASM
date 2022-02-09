package com.mars.infra.asm.learning.test;

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
class HelloWorld_5 {

    public int add(int a, int b) {
        int c = a + b;
        test(a, b, c);
        return c;
    }

    public int sub(int a, int b) {
        int c = a - b;
        test(a, b, c);
        return c;
    }

    public int mul(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        return a / b;
    }

    public void test(int a, int b, int c) {
        String line = String.format("a = %d, b = %d, c = %d", a, b, c);
        System.out.println(line);
    }
    
}
