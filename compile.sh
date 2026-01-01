#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo ""
echo "========================================"
echo "   学生信息管理系统 - 编译脚本"
echo "========================================"
echo ""

SRC_DIR="src"
OUT_DIR="out"
LIB_DIR="lib"
RES_DIR="resources"

echo -e "${BLUE}[INFO]${NC} 检查Java环境..."
if ! command -v javac &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} 未找到javac"
    exit 1
fi
echo -e "${GREEN}[SUCCESS]${NC} $(javac -version 2>&1)"

echo -e "${BLUE}[INFO]${NC} [1/4] 清理输出目录..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

echo -e "${BLUE}[INFO]${NC} [2/4] 收集源文件..."
find "$SRC_DIR" -name "*.java" > sources.txt
FILE_COUNT=$(wc -l < sources.txt)
echo -e "${BLUE}[INFO]${NC} 找到 $FILE_COUNT 个Java源文件"

echo -e "${BLUE}[INFO]${NC} [3/4] 编译源代码..."

CLASSPATH=""
for jar in $LIB_DIR/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done
CLASSPATH="${CLASSPATH:1}"

# 关键修复：使用 -sourcepath 确保包结构正确
if javac -encoding UTF-8 -sourcepath "$SRC_DIR" -cp "$CLASSPATH" -d "$OUT_DIR" @sources.txt; then
    echo -e "${GREEN}[SUCCESS]${NC} 编译成功！"
else
    echo -e "${RED}[ERROR]${NC} 编译失败！"
    rm -f sources.txt
    exit 1
fi

echo -e "${BLUE}[INFO]${NC} [4/4] 复制资源文件..."
if [ -d "$RES_DIR" ]; then
    cp -r "$RES_DIR"/* "$OUT_DIR"/ 2>/dev/null || true
    echo -e "${GREEN}[SUCCESS]${NC} 资源文件已复制"
fi

rm -f sources.txt

CLASS_COUNT=$(find "$OUT_DIR" -name "*.class" | wc -l)
echo ""
echo "========================================"
echo -e "${GREEN}[SUCCESS]${NC} 编译完成！"
echo "========================================"
echo "输出目录: $OUT_DIR"
echo "编译文件: $CLASS_COUNT 个 .class 文件"
echo ""
