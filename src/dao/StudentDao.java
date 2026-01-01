package dao;

import entity.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问类
 */
public class StudentDao extends BaseDao<Student> {
    
    @Override
    protected Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentNo(rs.getString("student_no"));
        student.setName(rs.getString("name"));
        student.setGender(rs.getString("gender"));
        student.setBirthDate(rs.getDate("birth_date"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setAddress(rs.getString("address"));
        student.setClassId(rs.getObject("class_id") != null ? rs.getInt("class_id") : null);
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setStatus(rs.getString("status"));
        student.setCreateTime(rs.getTimestamp("create_time"));
        
        // 尝试获取关联的班级名称
        try {
            student.setClassName(rs.getString("class_name"));
        } catch (SQLException ignored) {}
        
        return student;
    }
    
    /**
     * 查询所有学生（带班级名称）
     */
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT s.*, c.class_name FROM tb_student s " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "ORDER BY s.student_no";
        return queryList(sql);
    }
    
    /**
     * 根据ID查询学生
     */
    public Student findById(Integer id) throws SQLException {
        String sql = "SELECT s.*, c.class_name FROM tb_student s " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "WHERE s.id = ?";
        return queryOne(sql, id);
    }
    
    /**
     * 根据学号查询学生
     */
    public Student findByStudentNo(String studentNo) throws SQLException {
        String sql = "SELECT s.*, c.class_name FROM tb_student s " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "WHERE s.student_no = ?";
        return queryOne(sql, studentNo);
    }
    
    /**
     * 根据班级查询学生
     */
    public List<Student> findByClassId(Integer classId) throws SQLException {
        String sql = "SELECT s.*, c.class_name FROM tb_student s " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "WHERE s.class_id = ? ORDER BY s.student_no";
        return queryList(sql, classId);
    }
    
    /**
     * 多条件查询学生
     */
    public List<Student> search(String studentNo, String name, Integer classId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.*, c.class_name FROM tb_student s ");
        sql.append("LEFT JOIN tb_class c ON s.class_id = c.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (studentNo != null && !studentNo.trim().isEmpty()) {
            sql.append("AND s.student_no LIKE ? ");
            params.add("%" + studentNo.trim() + "%");
        }
        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND s.name LIKE ? ");
            params.add("%" + name.trim() + "%");
        }
        if (classId != null) {
            sql.append("AND s.class_id = ? ");
            params.add(classId);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND s.status = ? ");
            params.add(status);
        }
        
        sql.append("ORDER BY s.student_no");
        
        return queryList(sql.toString(), params.toArray());
    }
    
    /**
     * 添加学生
     */
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO tb_student (student_no, name, gender, birth_date, phone, email, " +
                     "address, class_id, enrollment_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, student.getStudentNo(), student.getName(), student.getGender(),
                student.getBirthDate(), student.getPhone(), student.getEmail(), student.getAddress(),
                student.getClassId(), student.getEnrollmentDate(), 
                student.getStatus() != null ? student.getStatus() : "在读");
    }
    
    /**
     * 更新学生
     */
    public int update(Student student) throws SQLException {
        String sql = "UPDATE tb_student SET student_no = ?, name = ?, gender = ?, birth_date = ?, " +
                     "phone = ?, email = ?, address = ?, class_id = ?, enrollment_date = ?, status = ? " +
                     "WHERE id = ?";
        return executeUpdate(sql, student.getStudentNo(), student.getName(), student.getGender(),
                student.getBirthDate(), student.getPhone(), student.getEmail(), student.getAddress(),
                student.getClassId(), student.getEnrollmentDate(), student.getStatus(), student.getId());
    }
    
    /**
     * 删除学生
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_student WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 批量删除学生
     */
    public int deleteBatch(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        
        StringBuilder sql = new StringBuilder("DELETE FROM tb_student WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) sql.append(",");
        }
        sql.append(")");
        
        return executeUpdate(sql.toString(), ids.toArray());
    }
    
    /**
     * 检查学号是否存在
     */
    public boolean existsByStudentNo(String studentNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_student WHERE student_no = ?";
        return queryCount(sql, studentNo) > 0;
    }
    
    /**
     * 检查学号是否存在（排除指定ID）
     */
    public boolean existsByStudentNo(String studentNo, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_student WHERE student_no = ? AND id != ?";
        return queryCount(sql, studentNo, excludeId) > 0;
    }
    
    /**
     * 统计学生总数
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_student";
        return queryCount(sql);
    }
    
    /**
     * 按班级统计学生数量
     */
    public int countByClassId(Integer classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_student WHERE class_id = ?";
        return queryCount(sql, classId);
    }
}