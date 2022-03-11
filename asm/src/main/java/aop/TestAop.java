package aop;

/**
 * Created by Mars on 2022/3/11
 */
public abstract class TestAop {

    public static void main(String[] args) {
        sayHello();
    }

    abstract void test();

    /**
     * 替换Log.e方法
     */
    public static void sayHello() {
        Log.e("TAG-TestAop", "sayHello, 测试");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
