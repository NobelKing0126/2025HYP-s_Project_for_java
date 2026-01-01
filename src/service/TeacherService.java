package service;

import dao.TeacherDao;
import dao.UserDao;
import dao.CourseDao;
import entity.Teacher;
import util.ValidationUtil;
import util.DBUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * 教师业务服务类
 */
public class TeacherService {
    
    private TeacherDao teacherDao = new TeacherDao();
    private UserDao userDao = new UserDao();
    private CourseDao courseDao = new CourseDao();
    
    /**
     * 查询所有教师
     */
    public List<Teacher> findAll() throws SQLException {
        return teacherDao.findAll();
    }
    
    /**
     * 根据ID查询教师
     */
    public Teacher findById(Integer id) throws SQLException {
        return teacherDao.findById(id);
    }
    
    /**
     * 根据工号查询教师
     */
    public Teacher findByTeacherNo(String teacherNo) throws SQLException {
        return teacherDao.findByTeacherNo(teacherNo);
    }
    
    /**
     * 按院系查询教师
     */
    public List<Teacher> findByDepartment(String department) throws SQLException {
        return teacherDao.findByDepartment(department);
    }
    
    /**
     * 添加教师
     */
    public int addTeacher(Teacher teacher, boolean createAccount) throws SQLException {
        // 数据验证
        validateTeacher(teacher, true);
        
        try {
            DBUtil.beginTransaction();
            
            // 插入教师记录
            int teacherId = teacherDao.insert(teacher);
            
            // 是否同时创建账户
            if (createAccount && teacherId > 0) {
                UserService userService = new UserService();
                userService.createTeacherAccount(teacherId, teacher.getTeacherNo());
            }
            
            DBUtil.commit();
            return teacherId;
            
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }
    
    /**
     * 更新教师信息
     */
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        // 数据验证
        validateTeacher(teacher, false);
        
        return teacherDao.update(teacher) > 0;
    }
    
    /**
     * 删除教师
     */
    public boolean deleteTeacher(Integer id) throws SQLException {
        try {
            DBUtil.beginTransaction();
            
            // 获取教师信息
            Teacher teacher = teacherDao.findById(id);
            if (teacher == null) {
                throw new IllegalArgumentException("教师不存在");
            }
            
            // 检查是否有课程关联
            if (!courseDao.findByTeacherId(id).isEmpty()) {
                throw new IllegalArgumentException("该教师还有关联的课程，无法删除");
            }
            
            // 删除相关的用户账户
            if (userDao.existsByUsername(teacher.getTeacherNo())) {
                userDao.findByRole("teacher").stream()
                    .filter(u -> teacher.getTeacherNo().equals(u.getUsername()))
                    .findFirst()
                    .ifPresent(u -> {
                        try {
                            userDao.delete(u.getId());
                        } catch (SQLException ignored) {}
                    });
            }
            
            // 删除教师
            int result = teacherDao.delete(id);
            
            DBUtil.commit();
            return result > 0;
            
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        }
    }
    
    /**
     * 验证教师数据
     */
    private void validateTeacher(Teacher teacher, boolean isNew) throws SQLException {
        if (!ValidationUtil.isNotEmpty(teacher.getTeacherNo())) {
            throw new IllegalArgumentException("工号不能为空");
        }
        if (!ValidationUtil.isValidTeacherNo(teacher.getTeacherNo())) {
            throw new IllegalArgumentException("工号格式不正确，应为T+3位数字");
        }
        if (!ValidationUtil.isNotEmpty(teacher.getName())) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (!ValidationUtil.isValidPhone(teacher.getPhone())) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        if (!ValidationUtil.isValidEmail(teacher.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        // 检查工号唯一性
        if (isNew) {
            if (teacherDao.existsByTeacherNo(teacher.getTeacherNo())) {
                throw new IllegalArgumentException("工号已存在");
            }
        } else {
            if (teacherDao.existsByTeacherNo(teacher.getTeacherNo(), teacher.getId())) {
                throw new IllegalArgumentException("工号已被其他教师使用");
            }
        }
    }
}