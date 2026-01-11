@echo off
echo Compiling Habbo Figure Renderer...

if not exist "bin" mkdir bin

javac -d bin src/main/java/com/habbo/figure/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run: run.bat "figure-string" [output.png] [direction] [action]
    echo Example: run.bat "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80" figure.png 2 std
) else (
    echo Compilation failed!
)
