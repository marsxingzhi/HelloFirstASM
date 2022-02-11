package tree;

/**
 * Created by JohnnySwordMan on 2/11/22
 */
public class HelloWorld {

    public int _intValue;

    // ASM：删除
    public String _strValue;

//    private Object _objValue;  ASM：添加

    // ASM：删除
    public String test(int a, long b, Object obj) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ASM：添加
//    public int generateMul(int a, int b) {
//        return a * b;
//    }

    public int add(int a, int b) {
        return a + b;
    }
}
