package dao;


import entity.Teacher;
import java.sql.*;
import java.util.List;

/**
 * 教师数据访问类
 */
public class TeacherDao extends BaseDao<Teacher> {
    
    @Override
    protected Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt("id"));
        teacher.setTeacherNo(rs.getString("teacher_no"));
        teacher.setName(rs.getString("name"));
        teacher.setGender(rs.getString("gender"));
        teacher.setPhone(rs.getString("phone"));
        teacher.setEmail(rs.getString("email"));
        teacher.setDepartment(rs.getString("department"));
        teacher.setTitle(rs.getString("title"));
        teacher.setCreateTime(rs.getTimestamp("create_time"));
        return teacher;
    }
    
    /**
     * 查询所有教师
     */
    public List<Teacher> findAll() throws SQLException {
        String sql = "SELECT * FROM tb_teacher ORDER BY teacher_no";
        return queryList(sql);
    }
    
    /**
     * 根据ID查询教师
     */
    public Teacher findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM tb_teacher WHERE id = ?";
        return queryOne(sql, id);
    }
    
    /**
     * 根据工号查询教师
     */
    public Teacher findByTeacherNo(String teacherNo) throws SQLException {
        String sql = "SELECT * FROM tb_teacher WHERE teacher_no = ?";
        return queryOne(sql, teacherNo);
    }
    
    /**
     * 按院系查询教师
     */
    public List<Teacher> findByDepartment(String department) throws SQLException {
        String sql = "SELECT * FROM tb_teacher WHERE department = ? ORDER BY teacher_no";
        return queryList(sql, department);
    }
    
    /**
     * 添加教师
     */
    public int insert(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO tb_teacher (teacher_no, name, gender, phone, email, department, title) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, teacher.getTeacherNo(), teacher.getName(), teacher.getGender(),
                teacher.getPhone(), teacher.getEmail(), teacher.getDepartment(), teacher.getTitle());
    }
    
    /**
     * 更新教师
     */
    public int update(Teacher teacher) throws SQLException {
        String sql = "UPDATE tb_teacher SET teacher_no = ?, name = ?, gender = ?, phone = ?, " +
                     "email = ?, department = ?, title = ? WHERE id = ?";
        return executeUpdate(sql, teacher.getTeacherNo(), teacher.getName(), teacher.getGender(),
                teacher.getPhone(), teacher.getEmail(), teacher.getDepartment(), 
                teacher.getTitle(), teacher.getId());
    }
    
    /**
     * 删除教师
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_teacher WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 检查工号是否存在
     */
    public boolean existsByTeacherNo(String teacherNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_teacher WHERE teacher_no = ?";
        return queryCount(sql, teacherNo) > 0;
    }
    
    /**
     * 检查工号是否存在（排除指定ID）
     */
    public boolean existsByTeacherNo(String teacherNo, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_teacher WHERE teacher_no = ? AND id != ?";
        return queryCount(sql, teacherNo, excludeId) > 0;
    }
}