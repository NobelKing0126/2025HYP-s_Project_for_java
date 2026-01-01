package service;


import dao.UserDao;
import dao.StudentDao;
import dao.TeacherDao;
import entity.User;
import entity.Student;
import entity.Teacher;
import java.sql.SQLException;

/**
 * 用户业务服务类
 */
public class UserService {
    
    private UserDao userDao = new UserDao();
    private StudentDao studentDao = new StudentDao();
    private TeacherDao teacherDao = new TeacherDao();
    
    /**
     * 用户登录验证
     * @return 登录成功返回用户对象，失败返回null
     */
    public User login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        User user = userDao.findByUsernameAndPassword(username.trim(), password);
        if (user != null) {
            // 获取用户真实姓名
            loadRealName(user);
        }
        return user;
    }
    
    /**
     * 加载用户真实姓名
     */
    private void loadRealName(User user) throws SQLException {
        if (user.isAdmin()) {
            user.setRealName("系统管理员");
        } else if (user.isTeacher() && user.getRelatedId() != null) {
            Teacher teacher = teacherDao.findById(user.getRelatedId());
            if (teacher != null) {
                user.setRealName(teacher.getName());
            }
        } else if (user.isStudent() && user.getRelatedId() != null) {
            Student student = studentDao.findById(user.getRelatedId());
            if (student != null) {
                user.setRealName(student.getName());
            }
        }
    }
    
    /**
     * 修改密码
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        
        // 验证旧密码
        User user = userDao.findByUsernameAndPassword(
            userDao.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .map(User::getUsername)
                .orElse(""), 
            oldPassword
        );
        
        if (user == null || !user.getId().equals(userId)) {
            throw new IllegalArgumentException("原密码不正确");
        }
        
        return userDao.updatePassword(userId, newPassword) > 0;
    }
    
    /**
     * 创建学生账户
     */
    public int createStudentAccount(Integer studentId, String studentNo) throws SQLException {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(studentNo)) {
            throw new IllegalArgumentException("该学号的账户已存在");
        }
        
        User user = new User();
        user.setUsername(studentNo);
        user.setPassword("123456"); // 默认密码
        user.setRole("student");
        user.setRelatedId(studentId);
        user.setStatus(1);
        
        return userDao.insert(user);
    }
    
    /**
     * 创建教师账户
     */
    public int createTeacherAccount(Integer teacherId, String teacherNo) throws SQLException {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(teacherNo)) {
            throw new IllegalArgumentException("该工号的账户已存在");
        }
        
        User user = new User();
        user.setUsername(teacherNo);
        user.setPassword("123456"); // 默认密码
        user.setRole("teacher");
        user.setRelatedId(teacherId);
        user.setStatus(1);
        
        return userDao.insert(user);
    }
    
    /**
     * 重置密码
     */
    public boolean resetPassword(Integer userId) throws SQLException {
        return userDao.updatePassword(userId, "123456") > 0;
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Integer userId) throws SQLException {
        return userDao.delete(userId) > 0;
    }
}