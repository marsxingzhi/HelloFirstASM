### 一、修改类信息    
- 修改类的编译版本、类名以及实现Cloneable接口，代码可参考：`ClassChangeVisitor`   
### 二、删除类字段  
- 删除类中的strValue字段，代码可参考：`ClassFieldRemoveVisitor`  
### 三、添加类字段    
- 在类中添加字段objValue，代码可参考：`ClassFieldAddVisitor`       
### 四、删除类方法   
- 删除HelloFirstAsm类中的add方法，代码可参考：`ClassMethodRemoveVisitor`    
### 五、添加类方法    
- 添加HelloFirstAsm类中的mul方法，代码可参考：`ClassMethodAddVisitor`
### 六、修改方法-添加-进入和退出
- 在方法开始和结束的时候，添加打印语句，代码可参考：`EnterExitAdapter`  

### 七、修改方法-添加-打印方法参数和返回值 
- 打印方法入参和返回值，代码可参考：`ClassMethodParamPrintVisitor`、`MethodParamPrintAdvanceAdapter`   
  
### 八、方法耗时     
- 定义timer字段，timer值表示该方法的耗时
- 在方法开始时，添加`timer -= System.currentTimeMillis()`
- 在方法结束时，添加`timer += System.currentTimeMillis()`

详细代码可参考：`ComputeTimeAdapter`

### 九、清空方法体     
- 对应的MethodVisitor返回null
- 构造一个方法名和描述符相同的空方法   

详细代码可参考：`ClassClearMethodVisitor`

### 十、替换方法调用   
- 替换Instruction前后，需要保证操作数栈一致   

详细代码可参考：`ClassMethodInvokeReplaceVisitor`   

### 十一、查找-方法调用        
- 查找方法A中调用了哪些方法，例如：`ClassMethodInvokeFindVisitor`
- 查找方法A被哪些方法调用了，例如：`ClassMethodInvokeFindV2Visitor`   

### 十二、优化方法-删除加0   
```Java
// 原方法
public void test4(int var1, int var2) {
    int var3 = var1 + var2;
    int var4 = var3 + 0;
    System.out.println(var4);
}

// 优化后，注意：其实我这里只是删除了加0的Instruction
public void test4(int var1, int var2) {
    int var3 = var1 + var2;
    System.out.println(var3);
}
```  
详细代码可参考：`ClassMethodOptVisitor` 

### 十三、优化方法-删除打印语句  
```Java
public void test5(int a, int b) {
    System.out.println("嘻嘻-☺️");  // 待删除
    int c = a + b;
    System.out.println("哈哈哈-😄");  // 待删除
    System.out.println(c);
}
```   
详细代码可参考：`ClassMethodRemovePrintVisitor`        

### 十四、优化方法-删除无意义的语句    
```Java
public void test6(int a, int b) {
    int c = a + b;
    this.val = this.val;  // 待删除
    System.out.println(c);
}
```     
详细代码可参考：`ClassMethodRemoveInsnVisitor`
