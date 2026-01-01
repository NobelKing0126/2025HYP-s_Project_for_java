#!/bin/bash

echo ""
echo "========================================"
echo "   学生信息管理系统 - 启动"
echo "========================================"
echo ""

# 检查编译输出
if [ ! -d "out" ]; then
    echo "[ERROR] 请先运行 ./compile.sh 编译项目！"
    exit 1
fi

# 构建classpath
CLASSPATH="out"
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

echo "[INFO] 正在启动程序..."
echo ""

java -Dfile.encoding=UTF-8 -cp "$CLASSPATH" Main

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] 程序异常退出"
fi
