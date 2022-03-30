package run.test;

/**
 * Created by Mars on 2022/3/22
 */
public class ProxyInsnChain {

    public static Object handle(Object... args) {
        return null;
    }

    public static Object handle1(String param1, String param2, int code) {
        return null;
    }

    // test two params
    public static Object proceed(String param1, String param2) {
        return null;
    }

    public static Object proceed(Object obj, String param1, String param2) {
        return null;
    }

    public static int proceed1(String param1, String param2) {
        return 0;
    }

}
