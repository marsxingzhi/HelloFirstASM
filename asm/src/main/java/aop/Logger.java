package aop;

/**
 * Created by Mars on 2022/3/11
 */
public class Logger {

    public static void ee(String tag, String msg) {
        System.out.println(tag+" , " + msg + ", 这是我hook的方法");
    }

}
