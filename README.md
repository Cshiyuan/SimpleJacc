# SimpleJacc

**基于 LL(1) 文法的简单 Yacc（编译原理课程大作业）**

一个用 Java 实现的简易 Yacc 工具。读取 BNF 语法定义文件，自动生成 LL(1) 分析器，能判断输入的单词流是否符合该文法。若输入文法不是 LL(1) 文法则报错提示。

## 功能

- 解析 BNF 语法定义文件
- 自动构建 LL(1) 分析表
- 生成 Java 语言的 LL(1) 语法分析器
- 非 LL(1) 文法检测与报错
- 附带 10 组测试用例

## 使用

```bash
# 编译
javac -classpath lib/ -d bin/ -sourcepath src @sourcelist.txt

# 运行
java -cp bin/ simplejacc.Main
```

## 项目结构

```
├── src/simplejacc/   源码（BnfTokenizer / Grammar / Main）
├── testcases/        测试用例
├── lib/              依赖库
└── run.bat           Windows 一键编译运行脚本
```

## 技术栈

- **语言**: Java
- **文法类型**: LL(1)
