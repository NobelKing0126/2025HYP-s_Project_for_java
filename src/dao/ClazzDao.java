package dao;

import entity.Clazz;
import java.sql.*;
import java.util.List;

/**
 * 班级数据访问类
 */
public class ClazzDao extends BaseDao<Clazz> {
    
    @Override
    protected Clazz mapRow(ResultSet rs) throws SQLException {
        Clazz clazz = new Clazz();
        clazz.setId(rs.getInt("id"));
        clazz.setClassName(rs.getString("class_name"));
        clazz.setGrade(rs.getString("grade"));
        clazz.setMajor(rs.getString("major"));
        clazz.setDepartment(rs.getString("department"));
        clazz.setCreateTime(rs.getTimestamp("create_time"));
        
        // 尝试获取学生数量
        try {
            clazz.setStudentCount(rs.getInt("student_count"));
        } catch (SQLException ignored) {}
        
        return clazz;
    }
    
    /**
     * 查询所有班级
     */
    public List<Clazz> findAll() throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id) AS student_count " +
                     "FROM tb_class c ORDER BY c.grade DESC, c.class_name";
        return queryList(sql);
    }
    
    /**
     * 根据ID查询班级
     */
    public Clazz findById(Integer id) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id) AS student_count " +
                     "FROM tb_class c WHERE c.id = ?";
        return queryOne(sql, id);
    }
    
    /**
     * 根据班级名称查询
     */
    public Clazz findByClassName(String className) throws SQLException {
        String sql = "SELECT * FROM tb_class WHERE class_name = ?";
        return queryOne(sql, className);
    }
    
    /**
     * 按年级查询班级
     */
    public List<Clazz> findByGrade(String grade) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id) AS student_count " +
                     "FROM tb_class c WHERE c.grade = ? ORDER BY c.class_name";
        return queryList(sql, grade);
    }
    
    /**
     * 按院系查询班级
     */
    public List<Clazz> findByDepartment(String department) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id) AS student_count " +
                     "FROM tb_class c WHERE c.department = ? ORDER BY c.grade DESC, c.class_name";
        return queryList(sql, department);
    }
    
    /**
     * 添加班级
     */
    public int insert(Clazz clazz) throws SQLException {
        String sql = "INSERT INTO tb_class (class_name, grade, major, department) VALUES (?, ?, ?, ?)";
        return executeInsert(sql, clazz.getClassName(), clazz.getGrade(), 
                clazz.getMajor(), clazz.getDepartment());
    }
    
    /**
     * 更新班级
     */
    public int update(Clazz clazz) throws SQLException {
        String sql = "UPDATE tb_class SET class_name = ?, grade = ?, major = ?, department = ? WHERE id = ?";
        return executeUpdate(sql, clazz.getClassName(), clazz.getGrade(), 
                clazz.getMajor(), clazz.getDepartment(), clazz.getId());
    }
    
    /**
     * 删除班级
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_class WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 检查班级名称是否存在
     */
    public boolean existsByClassName(String className) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_class WHERE class_name = ?";
        return queryCount(sql, className) > 0;
    }
    
    /**
     * 检查班级名称是否存在（排除指定ID）
     */
    public boolean existsByClassName(String className, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_class WHERE class_name = ? AND id != ?";
        return queryCount(sql, className, excludeId) > 0;
    }
    
    /**
     * 检查班级下是否有学生
     */
    public boolean hasStudents(Integer classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_student WHERE class_id = ?";
        return queryCount(sql, classId) > 0;
    }
    
    /**
     * 获取所有年级列表
     */
    public List<String> findAllGrades() throws SQLException {
        String sql = "SELECT DISTINCT grade FROM tb_class ORDER BY grade DESC";
        List<Clazz> list = queryList(sql);
        return list.stream().map(Clazz::getGrade).collect(java.util.stream.Collectors.toList());
    }
}