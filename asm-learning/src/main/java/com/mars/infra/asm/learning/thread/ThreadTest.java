package com.mars.infra.asm.learning.thread;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTest {

    public void query() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行任务");
            }
        });
        thread.start();
    }
}
