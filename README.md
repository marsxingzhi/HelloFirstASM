# README    



### TODO   

- [x] 简单demo，插入println方法，详见demo:asm-plugin
- [ ] 计算方法耗时的gradle plugin
- [ ] 仿照Hunter，将它的demo都实现一遍


### 方法耗时gradle plugin      

- [x] 方法耗时，简单实现
- [ ] 新增Extension，添加属性enable，是否开启方法耗时插入
- [ ] 整理，好好整理，就目前阶段，不要求完全看懂字节码，或者说熟练写出字节码，但是需要知道字节码的基本知识

### Label    
Label的使用可以分成三个步骤：
1. 创建Label对象
2. 确定Label位置，`mv.visitLabel`
3. 使用跳转方法，建立联系，例如：`mv.visitJumpInsn`

### Thread-Task  
线程替换，将系统线程替换成自定义的线程。
- [ ] new Thread