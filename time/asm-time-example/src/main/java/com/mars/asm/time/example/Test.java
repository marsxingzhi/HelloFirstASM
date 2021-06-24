package com.mars.asm.time.example;

import com.mars.asm.time.library.TimeManager;

/**
 * Created by geyan on 2021/6/25
 */
public class Test {

    public void computeMethod() {
        long start = System.currentTimeMillis();
        mockQuery();
        TimeManager.INSTANCE.timeMethod("xxx", "yyy", System.currentTimeMillis() - start);
    }

    private void mockQuery() {

    }
}
