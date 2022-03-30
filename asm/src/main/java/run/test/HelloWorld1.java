package run.test;

import org.objectweb.asm.Type;

/**
 * Created by Mars on 2022/3/24
 */
class HelloWorld1 {

    public static boolean hookInit(String username, String password, int code) {
        Boolean res = (Boolean) ProxyInsnChain.handle(username, password, code);
        return true;
    }

    public static void hookInit_1(String username, String password, int code) {
        ProxyInsnChain.handle1(username, password, code);
    }

    public static int hookLogW(String tag, String msg) {
        int value = (int) ProxyInsnChain.proceed(tag, msg + " ---> TestMixin hook hookLogW success.");
        return value;
    }

    public static int hookLogW_1(String tag, String msg) {
        return ProxyInsnChain.proceed1(tag, msg + " ---> hook success");
    }


    public static void hookLogin(Object obj, String username, String password) {
        System.out.println("hookLogin invoke.");
        Login login = (Login) obj;
        if (LoginUtils.check(username, password)) {
            login.login(username, password);
        }
    }

    public static void hookLogin_2(Object obj, String username, String password) {
        System.out.println("hookLogin_2 invoke.");
        Login login = (Login) obj;
        if (login.check(username, password)) {
            ProxyInsnChain.proceed(obj, username, password);
        }
    }
}
