#!/bin/bash

# ========================================
# 学生信息管理系统 - 打包脚本
# ========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo ""
echo "========================================"
echo "   学生信息管理系统 - 打包脚本"
echo "========================================"
echo ""

# 变量设置
OUT_DIR="out"
DIST_DIR="dist"
JAR_NAME="StudentManagementSystem.jar"
VERSION="1.0"

# 步骤1：先编译
print_info "[1/7] 编译项目..."
./compile.sh
if [ $? -ne 0 ]; then
    print_error "编译失败，打包终止"
    exit 1
fi
echo ""

# 步骤2：创建发布目录
print_info "[2/7] 创建发布目录..."
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"
mkdir -p "$DIST_DIR/lib"
mkdir -p "$DIST_DIR/resources"
mkdir -p "$DIST_DIR/sql"
mkdir -p "$DIST_DIR/logs"

# 步骤3：复制依赖库
print_info "[3/7] 复制依赖库..."
cp lib/*.jar "$DIST_DIR/lib/"
LIB_COUNT=$(ls -1 "$DIST_DIR/lib/"*.jar | wc -l)
print_success "已复制 $LIB_COUNT 个依赖库"

# 步骤4：复制资源文件
print_info "[4/7] 复制资源文件..."
if [ -d "resources" ]; then
    cp -r resources/* "$DIST_DIR/resources/" 2>/dev/null || true
fi

# 步骤5：复制SQL脚本
print_info "[5/7] 复制SQL脚本..."
if [ -d "sql" ]; then
    cp -r sql/* "$DIST_DIR/sql/" 2>/dev/null || true
fi

# 步骤6：打包JAR
print_info "[6/7] 打包JAR文件..."

# 检查MANIFEST.MF
if [ ! -f "MANIFEST.MF" ]; then
    print_error "MANIFEST.MF 不存在，请先创建"
    exit 1
fi

# 进入out目录打包
cd "$OUT_DIR"
jar cvfm "../$DIST_DIR/$JAR_NAME" ../MANIFEST.MF .
cd ..

print_success "JAR文件已创建: $DIST_DIR/$JAR_NAME"

# 步骤7：创建启动脚本
print_info "[7/7] 创建启动脚本..."

# Linux启动脚本
cat > "$DIST_DIR/start.sh" << 'STARTEOF'
#!/bin/bash
echo "正在启动学生信息管理系统..."
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 检查Java
if ! command -v java &> /dev/null; then
    echo "[ERROR] 未找到Java，请先安装JRE/JDK"
    exit 1
fi

# 运行程序
java -Dfile.encoding=UTF-8 -jar StudentManagementSystem.jar

if [ $? -ne 0 ]; then
    echo ""
    echo "程序运行出错！"
    read -p "按回车键退出..."
fi
STARTEOF
chmod +x "$DIST_DIR/start.sh"

# Windows启动脚本
cat > "$DIST_DIR/start.bat" << 'BATEOF'
@echo off
chcp 65001 >nul
echo 正在启动学生信息管理系统...
echo.
java -Dfile.encoding=UTF-8 -jar StudentManagementSystem.jar
if %errorlevel% neq 0 (
    echo.
    echo 程序运行出错！
    pause
)
BATEOF

# 创建README
cat > "$DIST_DIR/README.txt" << READMEEOF
========================================
    学生信息管理系统 V${VERSION}
========================================

【系统要求】
- JDK/JRE 1.8 或更高版本
- MySQL 5.7 或更高版本
- 图形界面环境（X11）

【安装步骤】

1. 安装并配置MySQL数据库

2. 创建数据库
   mysql -u root -p < sql/init.sql

3. 修改数据库配置
   编辑 resources/db.properties
   修改数据库地址、用户名、密码

4. 运行程序
   Linux/Mac: ./start.sh
   Windows:   双击 start.bat

【默认账号】
管理员: admin / 123456
教师:   T001 / 123456
学生:   2021001001 / 123456

【目录说明】
├── StudentManagementSystem.jar  主程序
├── lib/                         依赖库
├── resources/                   配置文件
├── sql/                         数据库脚本
├── logs/                        日志目录
├── start.sh                     Linux启动脚本
├── start.bat                    Windows启动脚本
└── README.txt                   本文件

【常见问题】

Q: 提示"数据库连接失败"
A: 1. 检查MySQL服务是否启动
   2. 检查db.properties配置是否正确
   3. 确认数据库已创建

Q: 中文显示乱码
A: 确保系统locale设置为UTF-8
   export LANG=zh_CN.UTF-8

Q: 无法显示图形界面
A: 1. 确保有X11环境
   2. 如果是SSH远程，需要启用X11转发
      ssh -X user@host

========================================
READMEEOF

# 统计打包结果
echo ""
echo "========================================"
print_success "打包完成！"
echo "========================================"
echo ""
echo "发布目录: $DIST_DIR"
echo ""
echo "目录内容:"
ls -la "$DIST_DIR"
echo ""
echo "JAR文件大小: $(du -h "$DIST_DIR/$JAR_NAME" | cut -f1)"
echo ""

# 提示下一步
echo "下一步操作:"
echo "  1. 进入发布目录: cd $DIST_DIR"
echo "  2. 运行程序: ./start.sh"
echo ""
