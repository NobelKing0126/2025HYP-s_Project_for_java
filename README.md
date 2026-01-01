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
