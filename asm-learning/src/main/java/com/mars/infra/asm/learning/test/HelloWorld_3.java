package com.mars.infra.asm.learning.test;

/**
 * Created by JohnnySwordMan on 2022/1/26
 */
public class HelloWorld_3 {

    public int intValue;
    public String strValue;  // 删除这个字段
    // 添加一个Object objValue字段

    // 添加Cloneable接口
    @Override
    public Object clone()  throws CloneNotSupportedException {
        return super.clone();
    }

    // 删除add方法
    public int add(int a, int b) {
        return a + b;
    }

    // 利用ASM添加
//    public int mul(int a, int b) {
//        return a * b;
//    }

    // 利用ASM在方法进入和退出时，打印日志
    public void test() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 需求：打印方法参数、返回值
    public int test1(String name, int age, long idCard, Object obj) {
//        System.out.println(name);
//        System.out.println(age);
//        System.out.println(idCard);
//        System.out.println(obj);

        int hashCode = 0;
        hashCode += name.hashCode();
        hashCode += age;
        hashCode += (int) (idCard % Integer.MAX_VALUE);
        hashCode += obj.hashCode();

//        System.out.println(hashCode);

        return hashCode;
    }

    // ASM：清空方法体
    public void verify(String username, String password) throws IllegalArgumentException {
        if ("zhangsan".equals(username) && "123456".equals(password)) {
            return;
        }
        throw new IllegalArgumentException("username or password is not correct");
    }
}
