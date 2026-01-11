#!/bin/bash
echo "Compiling Habbo Figure Renderer..."

mkdir -p bin

javac -d bin src/main/java/com/habbo/figure/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run: ./run.sh \"figure-string\" [output.png] [direction] [action]"
    echo "Example: ./run.sh \"hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80\" figure.png 2 std"
else
    echo "Compilation failed!"
fi
