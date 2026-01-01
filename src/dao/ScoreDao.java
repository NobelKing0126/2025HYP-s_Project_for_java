package dao;

import entity.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 成绩数据访问类
 */
public class ScoreDao extends BaseDao<Score> {
    
    @Override
    protected Score mapRow(ResultSet rs) throws SQLException {
        Score score = new Score();
        score.setId(rs.getInt("id"));
        score.setStudentId(rs.getInt("student_id"));
        score.setCourseId(rs.getInt("course_id"));
        score.setScore(rs.getObject("score") != null ? rs.getDouble("score") : null);
        score.setExamType(rs.getString("exam_type"));
        score.setExamDate(rs.getDate("exam_date"));
        score.setRecorderId(rs.getObject("recorder_id") != null ? rs.getInt("recorder_id") : null);
        score.setCreateTime(rs.getTimestamp("create_time"));
        score.setUpdateTime(rs.getTimestamp("update_time"));
        
        // 尝试获取关联字段
        try {
            score.setStudentNo(rs.getString("student_no"));
            score.setStudentName(rs.getString("student_name"));
            score.setClassName(rs.getString("class_name"));
            score.setCourseNo(rs.getString("course_no"));
            score.setCourseName(rs.getString("course_name"));
            score.setCredit(rs.getObject("credit") != null ? rs.getDouble("credit") : null);
            score.setTeacherName(rs.getString("teacher_name"));
        } catch (SQLException ignored) {}
        
        return score;
    }
    
    /**
     * 查询所有成绩（带详细信息）
     */
    public List<Score> findAll() throws SQLException {
        String sql = "SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, " +
                     "co.course_no, co.course_name, co.credit, t.name AS teacher_name " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "LEFT JOIN tb_teacher t ON co.teacher_id = t.id " +
                     "ORDER BY s.student_no, co.course_no";
        return queryList(sql);
    }
    
    /**
     * 根据ID查询成绩
     */
    public Score findById(Integer id) throws SQLException {
        String sql = "SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, " +
                     "co.course_no, co.course_name, co.credit, t.name AS teacher_name " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "LEFT JOIN tb_teacher t ON co.teacher_id = t.id " +
                     "WHERE sc.id = ?";
        return queryOne(sql, id);
    }
    
    /**
     * 根据学生ID查询成绩
     */
    public List<Score> findByStudentId(Integer studentId) throws SQLException {
        String sql = "SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, " +
                     "co.course_no, co.course_name, co.credit, t.name AS teacher_name " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "LEFT JOIN tb_teacher t ON co.teacher_id = t.id " +
                     "WHERE sc.student_id = ? ORDER BY co.course_no";
        return queryList(sql, studentId);
    }
    
    /**
     * 根据课程ID查询成绩
     */
    public List<Score> findByCourseId(Integer courseId) throws SQLException {
        String sql = "SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, " +
                     "co.course_no, co.course_name, co.credit, t.name AS teacher_name " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "LEFT JOIN tb_teacher t ON co.teacher_id = t.id " +
                     "WHERE sc.course_id = ? ORDER BY s.student_no";
        return queryList(sql, courseId);
    }
    
    /**
     * 根据教师ID查询其授课的成绩
     */
    public List<Score> findByTeacherId(Integer teacherId) throws SQLException {
        String sql = "SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, " +
                     "co.course_no, co.course_name, co.credit, t.name AS teacher_name " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "LEFT JOIN tb_class c ON s.class_id = c.id " +
                     "LEFT JOIN tb_teacher t ON co.teacher_id = t.id " +
                     "WHERE co.teacher_id = ? ORDER BY co.course_no, s.student_no";
        return queryList(sql, teacherId);
    }
    
    /**
     * 多条件查询成绩
     */
    public List<Score> search(String studentNo, String studentName, Integer classId,
                              Integer courseId, String examType, Double minScore, Double maxScore) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sc.*, s.student_no, s.name AS student_name, c.class_name, ");
        sql.append("co.course_no, co.course_name, co.credit, t.name AS teacher_name ");
        sql.append("FROM tb_score sc ");
        sql.append("JOIN tb_student s ON sc.student_id = s.id ");
        sql.append("JOIN tb_course co ON sc.course_id = co.id ");
        sql.append("LEFT JOIN tb_class c ON s.class_id = c.id ");
        sql.append("LEFT JOIN tb_teacher t ON co.teacher_id = t.id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (studentNo != null && !studentNo.trim().isEmpty()) {
            sql.append("AND s.student_no LIKE ? ");
            params.add("%" + studentNo.trim() + "%");
        }
        if (studentName != null && !studentName.trim().isEmpty()) {
            sql.append("AND s.name LIKE ? ");
            params.add("%" + studentName.trim() + "%");
        }
        if (classId != null) {
            sql.append("AND s.class_id = ? ");
            params.add(classId);
        }
        if (courseId != null) {
            sql.append("AND sc.course_id = ? ");
            params.add(courseId);
        }
        if (examType != null && !examType.trim().isEmpty()) {
            sql.append("AND sc.exam_type = ? ");
            params.add(examType);
        }
        if (minScore != null) {
            sql.append("AND sc.score >= ? ");
            params.add(minScore);
        }
        if (maxScore != null) {
            sql.append("AND sc.score <= ? ");
            params.add(maxScore);
        }
        
        sql.append("ORDER BY s.student_no, co.course_no");
        
        return queryList(sql.toString(), params.toArray());
    }
    
    /**
     * 添加成绩
     */
    public int insert(Score score) throws SQLException {
        String sql = "INSERT INTO tb_score (student_id, course_id, score, exam_type, exam_date, recorder_id) " + "VALUES (?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, score.getStudentId(), score.getCourseId(), score.getScore(),
                score.getExamType(), score.getExamDate(), score.getRecorderId());
    }
    
    /**
     * 更新成绩
     */
    public int update(Score score) throws SQLException {
        String sql = "UPDATE tb_score SET student_id = ?, course_id = ?, score = ?, " + "exam_type = ?, exam_date = ?, recorder_id = ? WHERE id = ?";
        return executeUpdate(sql, score.getStudentId(), score.getCourseId(), score.getScore(),
                score.getExamType(), score.getExamDate(), score.getRecorderId(), score.getId());
    }
    
    /**
     * 删除成绩
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_score WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 检查成绩记录是否存在（同一学生、同一课程、同一考试类型）
     */
    public boolean exists(Integer studentId, Integer courseId, String examType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_score WHERE student_id = ? AND course_id = ? AND exam_type = ?";
        return queryCount(sql, studentId, courseId, examType) > 0;
    }
    
    /**
     * 检查成绩记录是否存在（排除指定ID）
     */
    public boolean exists(Integer studentId, Integer courseId, String examType, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_score WHERE student_id = ? AND course_id = ? AND exam_type = ? AND id != ?";
        return queryCount(sql, studentId, courseId, examType, excludeId) > 0;
    }
    
    /**
     * 获取学生的平均成绩
     */
    public Double getAverageScoreByStudentId(Integer studentId) throws SQLException {
        String sql = "SELECT AVG(score) FROM tb_score WHERE student_id = ? AND score IS NOT NULL";
        Object result = queryScalar(sql, studentId);
        return result != null ? ((Number) result).doubleValue() : null;
    }
    
    /**
     * 获取课程的平均成绩
     */
    public Double getAverageScoreByCourseId(Integer courseId) throws SQLException {
        String sql = "SELECT AVG(score) FROM tb_score WHERE course_id = ? AND score IS NOT NULL";
        Object result = queryScalar(sql, courseId);
        return result != null ? ((Number) result).doubleValue() : null;
    }
    
    /**
     * 获取课程的最高分
     */
    public Double getMaxScoreByCourseId(Integer courseId) throws SQLException {
        String sql = "SELECT MAX(score) FROM tb_score WHERE course_id = ? AND score IS NOT NULL";
        Object result = queryScalar(sql, courseId);
        return result != null ? ((Number) result).doubleValue() : null;
    }
    
    /**
     * 获取课程的最低分
     */
    public Double getMinScoreByCourseId(Integer courseId) throws SQLException {
        String sql = "SELECT MIN(score) FROM tb_score WHERE course_id = ? AND score IS NOT NULL";
        Object result = queryScalar(sql, courseId);
        return result != null ? ((Number) result).doubleValue() : null;
    }
    
    /**
     * 获取课程的成绩分布统计
     */
    public Map<String, Integer> getScoreDistributionByCourseId(Integer courseId) throws SQLException {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = "SELECT " +
                     "SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) AS excellent, " +
                     "SUM(CASE WHEN score >= 80 AND score < 90 THEN 1 ELSE 0 END) AS good, " +
                     "SUM(CASE WHEN score >= 70 AND score < 80 THEN 1 ELSE 0 END) AS medium, " +
                     "SUM(CASE WHEN score >= 60 AND score < 70 THEN 1 ELSE 0 END) AS pass, " +
                     "SUM(CASE WHEN score < 60 THEN 1 ELSE 0 END) AS fail " +
                     "FROM tb_score WHERE course_id = ? AND score IS NOT NULL";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = util.DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                distribution.put("优秀(90-100)", rs.getInt("excellent"));
                distribution.put("良好(80-89)", rs.getInt("good"));
                distribution.put("中等(70-79)", rs.getInt("medium"));
                distribution.put("及格(60-69)", rs.getInt("pass"));
                distribution.put("不及格(<60)", rs.getInt("fail"));
            }
        } finally {
            util.DBUtil.close(rs, pstmt, conn);
        }
        return distribution;
    }
    
    /**
     * 获取班级各科平均分
     */
    public List<Object[]> getClassCourseAverages(Integer classId) throws SQLException {
        String sql = "SELECT co.course_name, AVG(sc.score) AS avg_score " +
                     "FROM tb_score sc " +
                     "JOIN tb_student s ON sc.student_id = s.id " +
                     "JOIN tb_course co ON sc.course_id = co.id " +
                     "WHERE s.class_id = ? AND sc.score IS NOT NULL " +
                     "GROUP BY co.id, co.course_name " +
                     "ORDER BY co.course_name";
        
        List<Object[]> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = util.DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, classId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("course_name"),
                    rs.getDouble("avg_score")
                });
            }
        } finally {
            util.DBUtil.close(rs, pstmt, conn);
        }
        return result;
    }
}