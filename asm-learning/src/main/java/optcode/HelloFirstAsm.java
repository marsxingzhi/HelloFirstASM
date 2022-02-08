package optcode;

/**
 * Created by JohnnySwordMan on 2/8/22
 */
public class HelloFirstAsm {

    static {
        Log.e("gy", "this is static code");
    }

    public void say() {
        Log.i("gy", "this is say method");
    }

    public void say2(String msg) {
        Log.i("gy", "this is say2 method, and msg is " + msg);
    }

    public static void build() {
        Log.d("gy", "this is build method");
    }

    public static void build1(String value) {
        Log.d("gy", "this is build1 method");
    }

    public static int build2(String key, String value) {
        Log.d("gy", "this is build2 method");
        return -1;
    }
}
