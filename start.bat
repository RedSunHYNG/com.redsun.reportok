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
    if exist "C:\Program Files\Common Files\Oracle\Java\javapath" (
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
)
endlocal
pause