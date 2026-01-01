package dao;

import entity.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问类
 */
public class CourseDao extends BaseDao<Course> {
    
    @Override
    protected Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setCourseNo(rs.getString("course_no"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredit(rs.getObject("credit") != null ? rs.getDouble("credit") : null);
        course.setHours(rs.getObject("hours") != null ? rs.getInt("hours") : null);
        course.setTeacherId(rs.getObject("teacher_id") != null ? rs.getInt("teacher_id") : null);
        course.setSemester(rs.getString("semester"));
        course.setCourseType(rs.getString("course_type"));
        course.setCreateTime(rs.getTimestamp("create_time"));
        
        // 尝试获取教师姓名
        try {
            course.setTeacherName(rs.getString("teacher_name"));
        } catch (SQLException ignored) {}
        
        return course;
    }
    
    /**
     * 查询所有课程（带教师姓名）
     */
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT c.*, t.name AS teacher_name FROM tb_course c " +
                     "LEFT JOIN tb_teacher t ON c.teacher_id = t.id " +
                     "ORDER BY c.course_no";
        return queryList(sql);
    }
    
    /**
     * 根据ID查询课程
     */
    public Course findById(Integer id) throws SQLException {
        String sql = "SELECT c.*, t.name AS teacher_name FROM tb_course c " +
                     "LEFT JOIN tb_teacher t ON c.teacher_id = t.id " +
                     "WHERE c.id = ?";
        return queryOne(sql, id);
    }
    
    /**
     * 根据课程编号查询
     */
    public Course findByCourseNo(String courseNo) throws SQLException {
        String sql = "SELECT c.*, t.name AS teacher_name FROM tb_course c " +
                     "LEFT JOIN tb_teacher t ON c.teacher_id = t.id " +
                     "WHERE c.course_no = ?";
        return queryOne(sql, courseNo);
    }
    
    /**
     * 根据教师ID查询课程
     */
    public List<Course> findByTeacherId(Integer teacherId) throws SQLException {
        String sql = "SELECT c.*, t.name AS teacher_name FROM tb_course c " +
                     "LEFT JOIN tb_teacher t ON c.teacher_id = t.id " +
                     "WHERE c.teacher_id = ? ORDER BY c.course_no";
        return queryList(sql, teacherId);
    }
    
    /**
     * 多条件查询课程
     */
    public List<Course> search(String courseNo, String courseName, Integer teacherId, 
                               String semester, String courseType) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, t.name AS teacher_name FROM tb_course c ");
        sql.append("LEFT JOIN tb_teacher t ON c.teacher_id = t.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (courseNo != null && !courseNo.trim().isEmpty()) {
            sql.append("AND c.course_no LIKE ? ");
            params.add("%" + courseNo.trim() + "%");
        }
        if (courseName != null && !courseName.trim().isEmpty()) {
            sql.append("AND c.course_name LIKE ? ");
            params.add("%" + courseName.trim() + "%");
        }
        if (teacherId != null) {
            sql.append("AND c.teacher_id = ? ");
            params.add(teacherId);
        }
        if (semester != null && !semester.trim().isEmpty()) {
            sql.append("AND c.semester = ? ");
            params.add(semester);
        }
        if (courseType != null && !courseType.trim().isEmpty()) {
            sql.append("AND c.course_type = ? ");
            params.add(courseType);
        }
        
        sql.append("ORDER BY c.course_no");
        
        return queryList(sql.toString(), params.toArray());
    }
    
    /**
     * 添加课程
     */
    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO tb_course (course_no, course_name, credit, hours, teacher_id, " +
                     "semester, course_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, course.getCourseNo(), course.getCourseName(), course.getCredit(),
                course.getHours(), course.getTeacherId(), course.getSemester(), course.getCourseType());
    }
    
    /**
     * 更新课程
     */
    public int update(Course course) throws SQLException {
        String sql = "UPDATE tb_course SET course_no = ?, course_name = ?, credit = ?, hours = ?, " +
                     "teacher_id = ?, semester = ?, course_type = ? WHERE id = ?";
        return executeUpdate(sql, course.getCourseNo(), course.getCourseName(), course.getCredit(),
                course.getHours(), course.getTeacherId(), course.getSemester(), 
                course.getCourseType(), course.getId());
    }
    
    /**
     * 删除课程
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_course WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 检查课程编号是否存在
     */
    public boolean existsByCourseNo(String courseNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_course WHERE course_no = ?";
        return queryCount(sql, courseNo) > 0;
    }
    
    /**
     * 检查课程编号是否存在（排除指定ID）
     */
    public boolean existsByCourseNo(String courseNo, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_course WHERE course_no = ? AND id != ?";
        return queryCount(sql, courseNo, excludeId) > 0;
    }
    
    /**
     * 检查课程下是否有成绩记录
     */
    public boolean hasScores(Integer courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_score WHERE course_id = ?";
        return queryCount(sql, courseId) > 0;
    }
    
    /**
     * 获取所有学期列表
     */
    public List<String> findAllSemesters() throws SQLException {
        String sql = "SELECT DISTINCT semester FROM tb_course WHERE semester IS NOT NULL ORDER BY semester DESC";
        List<Course> list = queryList(sql);
        return list.stream().map(Course::getSemester).collect(java.util.stream.Collectors.toList());
    }
}