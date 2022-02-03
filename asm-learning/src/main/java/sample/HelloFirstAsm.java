package sample;

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
        return a + b;
    }

    public int sub(int a, int b) {
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
}
