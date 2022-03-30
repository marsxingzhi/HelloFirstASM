package run.test;

import aop.Log;

/**
 * Created by Mars on 2022/3/16
 */
public class HelloWorld {


    private static int _generate_hookLogW_mixin(String tag, String msg) {
        return ((Integer) ProxyInsnChain.proceed(tag, msg + " ---> TestMixin hook hookLogW success.")).intValue();
    }

    public static int superE(String tag, String msg) {
        Log.e(tag, msg + " ---> 哈哈");
        return -1;
    }


    public static void login() {
        superE("HelloWorld", "login");
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void login2() {
        superE("HelloWorld", "login2");
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
