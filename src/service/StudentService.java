package service;

import dao.StudentDao;
import dao.UserDao;
import dao.ScoreDao;
import entity.Student;
import util.ValidationUtil;
import util.DBUtil;
import java.sql.SQLException;
import java.util.List;

/**
 * 学生业务服务类
 */
public class StudentService {
    
    private StudentDao studentDao = new StudentDao();
    private UserDao userDao = new UserDao();
    private ScoreDao scoreDao = new ScoreDao();
    
    /**
     * 查询所有学生
     */
    public List<Student> findAll() throws SQLException {
        return studentDao.findAll();
    }
    
    /**
     * 根据ID查询学生
     */
    public Student findById(Integer id) throws SQLException {
        return studentDao.findById(id);
    }
    
    /**
     * 根据学号查询学生
     */
    public Student findByStudentNo(String studentNo) throws SQLException {
        return studentDao.findByStudentNo(studentNo);
    }
    
    /**
     * 根据班级查询学生
     */
    public List<Student> findByClassId(Integer classId) throws SQLException {
        return studentDao.findByClassId(classId);
    }
    
    /**
     * 多条件搜索学生
     */
    public List<Student> search(String studentNo, String name, Integer classId, String status) throws SQLException {
        return studentDao.search(studentNo, name, classId, status);
    }
    
    /**
     * 添加学生
     */
    public int addStudent(Student student, boolean createAccount) throws SQLException {
        // 数据验证
        validateStudent(student, true);
        
        try {
            DBUtil.beginTransaction();
            
            // 插入学生记录
            int studentId = studentDao.insert(student);
            
            // 是否同时创建账户
            if (createAccount && studentId > 0) {
                UserService userService = new UserService();
                userService.createStudentAccount(studentId, student.getStudentNo());
            }
            
            DBUtil.commit();
            return studentId;
            
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }
    
    /**
     * 更新学生信息
     */
    public boolean updateStudent(Student student) throws SQLException {
        // 数据验证
        validateStudent(student, false);
        
        return studentDao.update(student) > 0;
    }
    
    /**
     * 删除学生
     */
    public boolean deleteStudent(Integer id) throws SQLException {
        try {
            DBUtil.beginTransaction();
            
            // 获取学生信息
            Student student = studentDao.findById(id);
            if (student == null) {
                throw new IllegalArgumentException("学生不存在");
            }
            
            // 删除相关的用户账户
            if (userDao.existsByUsername(student.getStudentNo())) {
                // 找到并删除对应的用户
                userDao.findByRole("student").stream()
                    .filter(u -> student.getStudentNo().equals(u.getUsername()))
                    .findFirst()
                    .ifPresent(u -> {
                        try {
                            userDao.delete(u.getId());
                        } catch (SQLException ignored) {}
                    });
            }
            
            // 删除学生（成绩记录会通过外键级联删除）
            int result = studentDao.delete(id);
            
            DBUtil.commit();
            return result > 0;
            
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }
    
    /**
     * 批量删除学生
     */
    public int deleteBatch(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        try {
            DBUtil.beginTransaction();
            
            // 删除相关用户账户
            for (Integer id : ids) {
                Student student = studentDao.findById(id);
                if (student != null && userDao.existsByUsername(student.getStudentNo())) {
                    userDao.findByRole("student").stream()
                        .filter(u -> student.getStudentNo().equals(u.getUsername()))
                        .findFirst()
                        .ifPresent(u -> {
                            try {
                                userDao.delete(u.getId());
                            } catch (SQLException ignored) {}
                        });
                }
            }
            
            int result = studentDao.deleteBatch(ids);
            
            DBUtil.commit();
            return result;
            
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }
    
    /**
     * 验证学生数据
     */
    private void validateStudent(Student student, boolean isNew) throws SQLException {
        if (!ValidationUtil.isNotEmpty(student.getStudentNo())) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (!ValidationUtil.isValidStudentNo(student.getStudentNo())) {
            throw new IllegalArgumentException("学号格式不正确，应为10位数字");
        }
        if (!ValidationUtil.isNotEmpty(student.getName())) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (!ValidationUtil.isValidPhone(student.getPhone())) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        // 检查学号唯一性
        if (isNew) {
            if (studentDao.existsByStudentNo(student.getStudentNo())) {
                throw new IllegalArgumentException("学号已存在");
            }
        } else {
            if (studentDao.existsByStudentNo(student.getStudentNo(), student.getId())) {
                throw new IllegalArgumentException("学号已被其他学生使用");
            }
        }
    }
    
    /**
     * 获取学生总数
     */
    public int getStudentCount() throws SQLException {
        return studentDao.count();
    }
    
    /**
     * 获取学生平均成绩
     */
    public Double getStudentAverageScore(Integer studentId) throws SQLException {
        return scoreDao.getAverageScoreByStudentId(studentId);
    }
}