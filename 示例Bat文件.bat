@echo off
setlocal

:: 设置要检查的JDK目录
set "JDK_DIR=C:\path\to\your\jdk"

:: 检查JDK目录是否存在，并且包含关键文件（如java.exe）
if exist "%JDK_DIR%\bin\java.exe" (
    :: 设置JAVA_HOME环境变量
    set "JAVA_HOME=%JDK_DIR%"
    
    :: 更新PATH环境变量以包含JDK的bin目录
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    
    :: 显示找到的JDK版本（可选）
    echo Found JDK at %JAVA_HOME%
    "%JAVA_HOME%\bin\java" -version
    
    :: 使用找到的JDK启动Java应用程序
    :: 替换为你的.jar文件路径和其他Java参数
    "%JAVA_HOME%\bin\java" -jar "C:\path\to\your\application.jar"
) else (
    echo JDK not found at %JDK_DIR%
    echo Please install JDK or update the JDK_DIR variable in this script.
)

:: 结束脚本
endlocal
pause

@echo off
setlocal
echo Ensure that com.redsun.reportok.jar exists in the same file directory.
echo At least 19 versions of JDK are required.
:: 设置要检查的JDK目录
:: set "JDK_DIR=.\jdk-21.0.5\bin"
:: "%JAVA_HOME%\bin\java"
if exist "%JAVA_HOME%\bin\java.exe" (
    echo Started the program using the JDK in the environment variable.
    java -jar com.redsun.reportok.jar
) else (
    echo JDK not found at environment variable.
    if exist ".\jdk\bin\java.exe" (
        echo Started the program using the JDK in the same directory.
        ".\jdk\bin\java.exe" -jar com.redsun.reportok.jar
    ) else (
        echo jdk not found at current directory.
        echo If JDK already exists in the same directory, please name the root directory of JDK as jdk.
        echo Please ensure that the JDK in the same directory exists, or that the computer has installed the JDK and configured the environment variables correctly.
    )
)
endlocal
pause