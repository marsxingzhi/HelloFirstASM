### 1. 删除类字段 
删除`_starValue`字段，代码可参考：[FieldRemoveClassNode](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/asm/src/main/java/tree/sample/FieldRemoveClassNode.kt) 
```Java
public class HelloWorld {
    // ASM：删除该字段
    public String _strValue;
}
```   
### 2. 添加类字段   
添加`_objValue`字段，代码可参考：[FieldAddClassNode](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/asm/src/main/java/tree/sample/FieldAddClassNode.kt)
```Java
public class HelloWorld {
    // ASM：添加该字段
    // public Object _objValue;
}
```   
### 3. 删除类方法   
删除`test`方法，代码可参考：[MethodRemoveClassNode](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/asm/src/main/java/tree/sample/MethodRemoveClassNode.kt)
```Java
// ASM：删除
public String test(int a, long b, Object obj) {
    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return null;
}
```  
### 4. 添加类方法 
添加`generateMul`方法，代码可参考：[MethodAddClassNode](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/asm/src/main/java/tree/sample/MethodAddClassNode.kt)
```Java
// ASM：添加
public int generateMul(int a, int b) {
    return a * b;
}
```   
### 5. 计算方法耗时
添加`timer`字段，然后在每个方法的开始加入`timer -= System.currentTimeMillis();`语句，结束加入`timer += System.currentTimeMillis();`，这样`timer`字段的值就对应该类中所有方法耗时的总和(当然也可以每个方法单独计算耗时)

代码可参考：[ComputeTimerClassNode](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/asm/src/main/java/tree/sample/ComputeTimerClassNode.kt)
```Java
public class HelloWorld {

    private static long timer;

    public void test() {
        timer -= System.currentTimeMillis();
        ...
        timer += System.currentTimeMillis();
    }
}
```