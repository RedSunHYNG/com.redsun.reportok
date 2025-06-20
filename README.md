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

FolderTraversal.java 第一代XTS报告审查工具：
将同类型(如属于CTS的)所有报告放在同一文件下，允许工具选择文件夹内的主报告然后等待就行。
一般遇到数据量较大的包时，运行中请耐心等待，可尝试回车防止卡死。

FolderTraversal_pro.java 第二代XTS报告审查工具：
特点：
- 能选择整查报告、单节查询，报告简化，工具对比更加全方位，优化逻辑减小性能需求，双语模式，优化语言准确度

本工具：
- 能做到：比对指纹，工具是否正常（比主工具版本高），查询主报告未通过的失败项
- 无法做到：查询未完整运行的模块里条例情况。
- 只针对自动化报告 
- CTS数据量较大，运行中请耐心等待，可尝试回车防止卡死。

现状:未完成，由于本人离职了软件测试，缺少基本资料用于设计程序逻辑和测试，所以此项目可能就此终结，已完成的部分已经发送。
