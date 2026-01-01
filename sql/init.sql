-- ========================================
-- 学生信息管理系统 - 数据库初始化脚本
-- ========================================

-- 创建数据库
DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE student_management;

-- ----------------------------------------
-- 用户表（登录用）
-- ----------------------------------------
CREATE TABLE tb_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role ENUM('admin', 'teacher', 'student') NOT NULL COMMENT '角色',
    related_id INT COMMENT '关联的教师ID或学生ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 班级表
-- ----------------------------------------
CREATE TABLE tb_class (
    id INT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    major VARCHAR(50) COMMENT '专业',
    department VARCHAR(50) COMMENT '院系',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 学生表
-- ----------------------------------------
CREATE TABLE tb_student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('男', '女') DEFAULT '男' COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(50) COMMENT '邮箱',
    address VARCHAR(200) COMMENT '家庭住址',
    class_id INT COMMENT '班级ID',
    enrollment_date DATE COMMENT '入学日期',
    status ENUM('在读', '休学', '毕业', '退学') DEFAULT '在读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 教师表
-- ----------------------------------------
CREATE TABLE tb_teacher (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_no VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('男', '女') DEFAULT '男',
    phone VARCHAR(20),
    email VARCHAR(50),
    department VARCHAR(50) COMMENT '所属院系',
    title VARCHAR(30) COMMENT '职称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 课程表
-- ----------------------------------------
CREATE TABLE tb_course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_no VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit DECIMAL(3,1) COMMENT '学分',
    hours INT COMMENT '学时',
    teacher_id INT COMMENT '授课教师',
    semester VARCHAR(20) COMMENT '开课学期',
    course_type ENUM('必修', '选修') DEFAULT '必修',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES tb_teacher(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 成绩表
-- ----------------------------------------
CREATE TABLE tb_score (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    score DECIMAL(5,2) COMMENT '成绩',
    exam_type ENUM('平时', '期中', '期末') DEFAULT '期末',
    exam_date DATE,
    recorder_id INT COMMENT '录入人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_course_type (student_id, course_id, exam_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------
-- 插入初始数据
-- ----------------------------------------

-- 管理员账户
INSERT INTO tb_user (username, password, role) VALUES ('admin', '123456', 'admin');

-- 班级数据
INSERT INTO tb_class (class_name, grade, major, department) VALUES 
('计科2101', '2021', '计算机科学与技术', '计算机学院'),
('计科2102', '2021', '计算机科学与技术', '计算机学院'),
('软工2101', '2021', '软件工程', '计算机学院'),
('软工2102', '2021', '软件工程', '计算机学院'),
('计科2201', '2022', '计算机科学与技术', '计算机学院');

-- 教师数据
INSERT INTO tb_teacher (teacher_no, name, gender, phone, department, title) VALUES 
('T001', '张教授', '男', '13800001111', '计算机学院', '教授'),
('T002', '李副教授', '女', '13800002222', '计算机学院', '副教授'),
('T003', '王讲师', '男', '13800003333', '计算机学院', '讲师'),
('T004', '赵老师', '女', '13800004444', '计算机学院', '讲师');

-- 教师账户
INSERT INTO tb_user (username, password, role, related_id) VALUES 
('T001', '123456', 'teacher', 1),
('T002', '123456', 'teacher', 2),
('T003', '123456', 'teacher', 3);

-- 学生数据
INSERT INTO tb_student (student_no, name, gender, birth_date, phone, class_id, enrollment_date) VALUES 
('2021001001', '张三', '男', '2003-05-15', '15900001111', 1, '2021-09-01'),
('2021001002', '李四', '女', '2003-08-20', '15900002222', 1, '2021-09-01'),
('2021001003', '王五', '男', '2003-03-10', '15900003333', 1, '2021-09-01'),
('2021001004', '赵六', '女', '2003-11-25', '15900004444', 1, '2021-09-01'),
('2021001005', '钱七', '男', '2003-07-08', '15900005555', 1, '2021-09-01'),
('2021002001', '孙八', '女', '2003-02-14', '15900006666', 2, '2021-09-01'),
('2021002002', '周九', '男', '2003-09-30', '15900007777', 2, '2021-09-01'),
('2021002003', '吴十', '女', '2003-12-05', '15900008888', 2, '2021-09-01'),
('2021003001', '郑十一', '男', '2003-04-18', '15900009999', 3, '2021-09-01'),
('2021003002', '王十二', '女', '2003-06-22', '15900010000', 3, '2021-09-01');

-- 学生账户
INSERT INTO tb_user (username, password, role, related_id) VALUES 
('2021001001', '123456', 'student', 1),
('2021001002', '123456', 'student', 2),
('2021001003', '123456', 'student', 3),
('2021001004', '123456', 'student', 4),
('2021001005', '123456', 'student', 5);

-- 课程数据
INSERT INTO tb_course (course_no, course_name, credit, hours, teacher_id, semester, course_type) VALUES 
('CS101', 'Java程序设计', 4.0, 64, 1, '2023-2024-1', '必修'),
('CS102', '数据库原理', 3.5, 56, 2, '2023-2024-1', '必修'),
('CS103', '数据结构', 4.0, 64, 1, '2023-2024-1', '必修'),
('CS104', '操作系统', 3.5, 56, 3, '2023-2024-1', '必修'),
('CS201', 'Web开发技术', 3.0, 48, 3, '2023-2024-2', '选修'),
('CS202', 'Python编程', 2.5, 40, 4, '2023-2024-2', '选修'),
('CS203', '软件工程', 3.0, 48, 2, '2023-2024-2', '必修');

-- 成绩数据
INSERT INTO tb_score (student_id, course_id, score, exam_type, exam_date, recorder_id) VALUES 
(1, 1, 85.5, '期末', '2024-01-15', 1),
(1, 2, 90.0, '期末', '2024-01-16', 2),
(1, 3, 78.5, '期末', '2024-01-17', 1),
(1, 4, 82.0, '期末', '2024-01-18', 3),
(2, 1, 92.0, '期末', '2024-01-15', 1),
(2, 2, 88.5, '期末', '2024-01-16', 2),
(2, 3, 95.0, '期末', '2024-01-17', 1),
(3, 1, 76.0, '期末', '2024-01-15', 1),
(3, 2, 58.5, '期末', '2024-01-16', 2),
(3, 3, 72.0, '期末', '2024-01-17', 1),
(4, 1, 95.0, '期末', '2024-01-15', 1),
(4, 2, 91.5, '期末', '2024-01-16', 2),
(5, 1, 82.5, '期末', '2024-01-15', 1),
(5, 2, 79.0, '期末', '2024-01-16', 2),
(6, 1, 88.0, '期末', '2024-01-15', 1),
(6, 2, 85.5, '期末', '2024-01-16', 2),
(7, 1, 67.5, '期末', '2024-01-15', 1),
(7, 2, 72.0, '期末', '2024-01-16', 2),
(8, 1, 91.0, '期末', '2024-01-15', 1),
(9, 1, 86.5, '期末', '2024-01-15', 1),
(10, 1, 79.0, '期末', '2024-01-15', 1);

-- ----------------------------------------
-- 创建视图
-- ----------------------------------------
CREATE OR REPLACE VIEW v_score_detail AS
SELECT 
    sc.id,
    s.student_no,
    s.name AS student_name,
    c.class_name,
    co.course_no,
    co.course_name,
    co.credit,
    sc.score,
    sc.exam_type,
    sc.exam_date,
    t.name AS teacher_name
FROM tb_score sc
JOIN tb_student s ON sc.student_id = s.id
JOIN tb_course co ON sc.course_id = co.id
LEFT JOIN tb_class c ON s.class_id = c.id
LEFT JOIN tb_teacher t ON co.teacher_id = t.id;

-- ----------------------------------------
-- 创建索引
-- ----------------------------------------
CREATE INDEX idx_student_no ON tb_student(student_no);
CREATE INDEX idx_student_class ON tb_student(class_id);
CREATE INDEX idx_score_student ON tb_score(student_id);
CREATE INDEX idx_score_course ON tb_score(course_id);
CREATE INDEX idx_teacher_no ON tb_teacher(teacher_no);
CREATE INDEX idx_course_no ON tb_course(course_no);

-- ----------------------------------------
-- 完成
-- ----------------------------------------
SELECT '数据库初始化完成！' AS message;
SELECT CONCAT('用户数量: ', COUNT(*)) AS info FROM tb_user;
SELECT CONCAT('班级数量: ', COUNT(*)) AS info FROM tb_class;
SELECT CONCAT('学生数量: ', COUNT(*)) AS info FROM tb_student;
SELECT CONCAT('教师数量: ', COUNT(*)) AS info FROM tb_teacher;
SELECT CONCAT('课程数量: ', COUNT(*)) AS info FROM tb_course;
SELECT CONCAT('成绩记录: ', COUNT(*)) AS info FROM tb_score;
