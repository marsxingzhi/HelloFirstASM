# README   

## 零、TODO  
- [ ] 线程
- [ ] 删除打印语句，例如：println、Log  


## 一、知识点
### Label    
Label的使用可以分成三个步骤：
1. 创建Label对象
2. 确定Label位置，`mv.visitLabel`
3. 使用跳转方法，建立联系，例如：`mv.visitJumpInsn`

## 二、示例   
- core api用法示例，可参考：[CORE-API-CASE](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/CORE-API-CASE.md)
- tree api用法示例，可参考：[TREE-API-CASE](https://github.com/JohnnySwordMan/HelloFirstASM/blob/master/TREE-API-CASE.md)

## 三、复杂示例(TODO内容)
### 1. Thread-Task  
线程替换，将系统线程替换成自定义的线程。
- [x] new Thread

