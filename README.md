# README    



### TODO   

- [x] 简单demo，插入println方法，详见demo:asm-plugin
- [ ] 计算方法耗时的gradle plugin
- [ ] 仿照Hunter，将它的demo都实现一遍
- [ ] 修改-替换方法调用
- [ ] 查找-方法调用
- [ ] 删除-清空方法体
- [ ] 删除-移除Instruction
- [ ] 添加-方法进入和退出


### 方法耗时gradle plugin      

- [x] 方法耗时，简单实现
- [ ] 新增Extension，添加属性enable，是否开启方法耗时插入
- [ ] 整理，好好整理，就目前阶段，不要求完全看懂字节码，或者说熟练写出字节码，但是需要知道字节码的基本知识

### Label    
Label的使用可以分成三个步骤：
1. 创建Label对象
2. 确定Label位置，`mv.visitLabel`
3. 使用跳转方法，建立联系，例如：`mv.visitJumpInsn`

### 示例   
#### 一、修改类信息    
- 修改类的编译版本、类名以及实现Cloneable接口，代码可参考：`ClassChangeVisitor`   
#### 二、删除类字段  
- 删除类中的strValue字段，代码可参考：`ClassFieldRemoveVisitor`  
#### 三、添加类字段    
- 在类中添加字段objValue，代码可参考：`ClassFieldAddVisitor`       
#### 四、删除类方法   
- 删除HelloFirstAsm类中的add方法，代码可参考：`ClassMethodRemoveVisitor`    
#### 五、添加类方法    
- 添加HelloFirstAsm类中的mul方法，代码可参考：`ClassMethodAddVisitor`
#### 六、修改方法-添加-进入和退出
- 在方法开始和结束的时候，添加打印语句，代码可参考：`EnterExitAdapter`  

#### 七、修改方法-添加-打印方法参数和返回值 
- 打印方法入参和返回值，代码可参考：`ClassMethodParamPrintVisitor`、`MethodParamPrintAdvanceAdapter`   
  
#### 八、方法耗时     
- 定义timer字段，timer值表示该方法的耗时
- 在方法开始时，添加`timer -= System.currentTimeMillis()`
- 在方法结束时，添加`timer += System.currentTimeMillis()`

详细代码可参考：`ComputeTimeAdapter`

#### 九、清空方法体     
- 对应的MethodVisitor返回null
- 构造一个方法名和描述符相同的空方法   

详细代码可参考：`ClassClearMethodVisitor`

#### 十、替换方法调用   
- 替换Instruction前后，需要保证操作数栈一致   

详细代码可参考：`ClassMethodInvokeReplaceVisitor`   

#### 十一、查找-方法调用        
- 查找方法A中调用了哪些方法，例如：`ClassMethodInvokeFindVisitor`
- 查找方法A被哪些方法调用了，例如：`ClassMethodInvokeFindV2Visitor`

### Thread-Task  
线程替换，将系统线程替换成自定义的线程。
- [x] new Thread