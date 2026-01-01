#!/bin/bash

# ========================================
# 学生信息管理系统 - 调试运行脚本
# ========================================

echo "========================================"
echo "   调试模式运行"
echo "========================================"

# 检查编译
if [ ! -f "out/Main.class" ]; then
    echo "[ERROR] 请先编译项目"
    exit 1
fi

# 构建classpath
CLASSPATH="out"
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

echo "CLASSPATH: $CLASSPATH"
echo ""
echo "启动程序..."
echo ""

# 带调试信息运行
java -Dfile.encoding=UTF-8 \
     -Djava.awt.headless=false \
     -verbose:class \
     -cp "$CLASSPATH" Main 2>&1 | head -100

# 如果不想看详细类加载信息，去掉 -verbose:class
