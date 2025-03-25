## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## 开发者的话

第二代XTS报告审查工具

特点：
- 能选择整查报告、单节查询，报告简化，工具对比更加全方位，优化逻辑减小性能需求，双语模式，优化语言准确度

本工具：
- 能做到：比对指纹，工具是否正常（比主工具版本高），查询主报告未通过的失败项
- 无法做到：查询未完整运行的模块里条例情况。
- 只针对自动化报告 
- CTS数据量较大，运行中请耐心等待，可尝试回车防止卡死。
