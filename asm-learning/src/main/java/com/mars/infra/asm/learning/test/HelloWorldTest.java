package com.mars.infra.asm.learning.test;

/**
 * Created by JohnnySwordMan on 2022/1/25
 */
public class HelloWorldTest {

    private long timer;

    public int mul(int a, int b) {
        return a * b;
    }

    public void query() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行任务");
            }
        });
        thread.start();
    }

    public void test() {
        timer -= System.currentTimeMillis();
        System.out.println("method start");
        System.out.println("this is a test method.");
        System.out.println("method end");
        timer += System.currentTimeMillis();
    }

    public int test(String name, int age, long idCard, Object obj) {
        System.out.println(name);
        int hashCode = 0;
        hashCode += name.hashCode();
        hashCode += age;
        hashCode += (int) (idCard % Integer.MAX_VALUE);
        hashCode += obj.hashCode();
        return hashCode;
    }
}
