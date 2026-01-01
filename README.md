基于 Java Swing + MySQL 的学生信息管理系统，支持管理员、教师、学生三种角色。

## 功能特性

- 🔐 分角色登录（管理员/教师/学生）
- 👨‍🎓 学生信息管理（增删改查）
- 📚 班级管理
- 📖 课程管理
- 📊 成绩管理与统计分析
- 📥 Excel导入导出

## 技术栈

- Java 11+
- Java Swing (GUI)
- MySQL 8.0
- JDBC

## 项目结构
```bash 
StudentManagementSystem/
├── src/
│ ├── Main.java # 程序入口
│ ├── entity/ # 实体类
│ ├── dao/ # 数据访问层
│ ├── service/ # 业务逻辑层
│ ├── ui/ # 界面层
│ └── util/ # 工具类
├── resources/
│ └── db.properties # 数据库配置
├── sql/
│ └── init.sql # 数据库初始化脚本
└── lib/ # 依赖库
```


## 安装与运行

### 1. 环境要求

- JDK 11 或更高版本
- MySQL 5.7 或更高版本

### 2. 数据库配置

```bash
# 初始化数据库
sudo mysql < sql/init.sql

# 创建用户
sudo mysql -e "CREATE USER 'stuadmin'@'localhost' IDENTIFIED BY 'Aa123456!'; GRANT ALL PRIVILEGES ON student_management.* TO 'stuadmin'@'localhost'; FLUSH PRIVILEGES;"
3. 修改配置文件
编辑 resources/db.properties，配置数据库连接信息。

4. 编译运行
Bash

./compile.sh
./run.sh
```
### 3. 如果出现数据库无法连接，则输入以下指令至终端
```bash
echo "将以下地址改为项目文件所在地址"
cd ~/桌面/program/StudentManagementSystem 

echo "===== 1. 检查MySQL ====="
sudo mysql -e "SELECT 'MySQL运行正常' AS status;"

echo ""
echo "===== 2. 创建用户 ====="
sudo mysql << 'SQLEOF'
DROP USER IF EXISTS 'stuadmin'@'localhost';
CREATE USER 'stuadmin'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Aa123456!';
GRANT ALL PRIVILEGES ON student_management.* TO 'stuadmin'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO 'stuadmin'@'localhost';
FLUSH PRIVILEGES;
SQLEOF

echo ""
echo "===== 3. 测试用户连接 ====="
mysql -u stuadmin -p'Aa123456!' -e "SELECT '用户连接成功' AS status;"

echo ""
echo "===== 4. 测试数据库 ====="
mysql -u stuadmin -p'Aa123456!' -e "USE student_management; SELECT COUNT(*) AS user_count FROM tb_user;"

echo ""
echo "===== 5. 更新配置文件 ====="
cat > resources/db.properties << 'EOF'
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
db.username=stuadmin
db.password=Aa123456!
EOF
cp resources/db.properties out/
echo "配置已更新"

echo ""
echo "===== 6. 启动程序 ====="
./run.sh

