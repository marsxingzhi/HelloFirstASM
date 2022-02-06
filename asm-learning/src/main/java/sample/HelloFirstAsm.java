package sample;

import java.util.Random;

/**
 * Created by JohnnySwordMan on 2022/2/1
 */
public class HelloFirstAsm {

    public int intValue;
    public String strValue;  // ASM：待删除字段
    // public Object objValue;  // ASM：待添加字段

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // ASM：删除add方法
    public int add(int a, int b) {
        test3(a, b, 0);
        return a + b;
    }

    public int sub(int a, int b) {
        test3(a, b, 0);
        return a - b;
    }

    // ASM：添加方法
//    public int mul(int a, int b) {
//        return a * b;
//    }

    // ASM：在方法开始和结束的地方添加一个语句
    public void test() {
        System.out.println("this is a test method.");
    }

    // ASM: 打印方法入参和返回值
    public int test(String name, int age, long idCard, Object obj) {
        int hashCode = 0;
        hashCode += name.hashCode();
        hashCode += age;
        hashCode += (int) (idCard % Integer.MAX_VALUE);
        hashCode += obj.hashCode();
        return hashCode;
    }

    // ASM：计算方法耗时
    public int add2(int a, int b) throws InterruptedException {
        int c = a + b;
        Random rand = new Random(System.currentTimeMillis());
        int num = rand.nextInt(300);
        Thread.sleep(100 + num);
        return c;
    }

    // ASM：清空方法体
    public void verify(String username, String password) throws IllegalArgumentException {
        if ("tomcat".equals(username) && "123456".equals(password)) {
            return;
        }
        throw new IllegalArgumentException("username or password is not correct");
    }

    // ASM：替换方法调用，将Math.max替换成Math.min、System.out.println替换成静态方法
    public void test(int a, int b) {
        test3(a, b, 0);
        int c = Math.max(a, b);
        System.out.println(c);
    }

    // ASM：查找该方法体内调用了哪些方法
    public void test1(int a, int b) {
        int c = Math.addExact(a, b);
        String line = String.format("%d + %d = %d", a, b, c);
        System.out.println(line);
    }

    // ASM：哪些方法调用了test3
    public void test3(int a, int b, int c) {
        String line = String.format("a = %d, b = %d, c = %d", a, b, c);
        System.out.println(line);
    }

    public int mock() {
        test3(1, 2, 3);
        return 1;
    }

    // ASM：优化代码，将int d = c + 0；改成int d = c；
    public void test4(int a, int b) {
        int c = a + b;
        int d = c + 0;
        System.out.println(d);
    }

    // 删除打印语句
    public void test5(int a, int b) {
        System.out.println("嘻嘻-☺️");
        int c = a + b;
        System.out.println("哈哈哈-😄");
        System.out.println(c);
    }
}
