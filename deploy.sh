#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() {
    echo -e "${CYAN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║           学生信息管理系统 - 一键部署工具                 ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_menu() {
    echo ""
    echo -e "${YELLOW}请选择操作：${NC}"
    echo "─────────────────────────────────────"
    echo "  [1] 检查环境"
    echo "  [2] 下载依赖库"
    echo "  [3] 初始化数据库 (sudo)"
    echo "  [4] 编译项目"
    echo "  [5] 运行项目"
    echo "  [6] 打包发布"
    echo "  [7] 清理编译文件"
    echo "  [8] 完整部署（2+3+4+5）"
    echo "  [0] 退出"
    echo "─────────────────────────────────────"
}

init_database() {
    echo ""
    echo -e "${BLUE}[初始化数据库]${NC}"
    echo ""
    
    if [ ! -f "sql/init.sql" ]; then
        echo -e "${RED}[ERROR] sql/init.sql 不存在${NC}"
        return 1
    fi
    
    echo "使用sudo执行数据库初始化..."
    if sudo mysql < sql/init.sql; then
        echo -e "${GREEN}[SUCCESS] 数据库初始化完成！${NC}"
        
        echo "创建应用程序用户..."
        sudo mysql -e "CREATE USER IF NOT EXISTS 'student_admin'@'localhost' IDENTIFIED BY '123456'; GRANT ALL PRIVILEGES ON student_management.* TO 'student_admin'@'localhost'; FLUSH PRIVILEGES;"
        
        echo "更新配置文件..."
        cat > resources/db.properties << 'PROPEOF'
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
db.username=student_admin
db.password=123456
PROPEOF
        echo -e "${GREEN}[SUCCESS] 配置完成！${NC}"
    else
        echo -e "${RED}[ERROR] 数据库初始化失败${NC}"
        return 1
    fi
}

check_environment() {
    echo ""
    echo -e "${BLUE}[检查环境]${NC}"
    echo -n "Java: "; java -version 2>&1 | head -1
    echo -n "MySQL: "; systemctl is-active mysql 2>/dev/null || echo "未运行"
    echo -n "依赖库: "; ls lib/*.jar 2>/dev/null | wc -l; echo "个jar文件"
    echo -n "编译状态: "; [ -f "out/Main.class" ] && echo "已编译" || echo "未编译"
    read -p "按回车继续..."
}

while true; do
    clear
    print_header
    print_menu
    read -p "请输入选项 [0-8]: " choice
    
    case $choice in
        1) check_environment ;;
        2) ./download_libs.sh; read -p "按回车继续..." ;;
        3) init_database; read -p "按回车继续..." ;;
        4) ./compile.sh; read -p "按回车继续..." ;;
        5) ./run.sh ;;
        6) ./package.sh; read -p "按回车继续..." ;;
        7) rm -rf out dist; echo "清理完成"; read -p "按回车继续..." ;;
        8) 
            ./download_libs.sh
            init_database
            ./compile.sh
            ./run.sh
            ;;
        0) exit 0 ;;
        *) echo "无效选项"; sleep 1 ;;
    esac
done
