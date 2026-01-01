package service;

import dao.ScoreDao;
import dao.StudentDao;
import dao.CourseDao;
import entity.Score;
import entity.Course;
import util.ValidationUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务服务类
 */
public class ScoreService {
    
    private ScoreDao scoreDao = new ScoreDao();
    private StudentDao studentDao = new StudentDao();
    private CourseDao courseDao = new CourseDao();
    
    /**
     * 查询所有成绩
     */
    public List<Score> findAll() throws SQLException {
        return scoreDao.findAll();
    }
    
    /**
     * 根据ID查询成绩
     */
    public Score findById(Integer id) throws SQLException {
        return scoreDao.findById(id);
    }
    
    /**
     * 根据学生ID查询成绩
     */
    public List<Score> findByStudentId(Integer studentId) throws SQLException {
        return scoreDao.findByStudentId(studentId);
    }
    
    /**
     * 根据课程ID查询成绩
     */
    public List<Score> findByCourseId(Integer courseId) throws SQLException {
        return scoreDao.findByCourseId(courseId);
    }
    
    /**
     * 根据教师ID查询成绩（只能查看自己教授的课程）
     */
    public List<Score> findByTeacherId(Integer teacherId) throws SQLException {
        return scoreDao.findByTeacherId(teacherId);
    }
    
    /**
     * 多条件搜索成绩
     */
    public List<Score> search(String studentNo, String studentName, Integer classId,
                              Integer courseId, String examType, Double minScore, Double maxScore) throws SQLException {
        return scoreDao.search(studentNo, studentName, classId, courseId, examType, minScore, maxScore);
    }
    
    /**
     * 添加成绩
     */
    public int addScore(Score score) throws SQLException {
        // 数据验证
        validateScore(score, true);
        
        return scoreDao.insert(score);
    }
    
    /**
     * 更新成绩
     */
    public boolean updateScore(Score score) throws SQLException {
        // 数据验证
        validateScore(score, false);
        
        return scoreDao.update(score) > 0;
    }
    
    /**
     * 删除成绩
     */
    public boolean deleteScore(Integer id) throws SQLException {
        return scoreDao.delete(id) > 0;
    }
    
    /**
     * 验证成绩数据
     */
    private void validateScore(Score score, boolean isNew) throws SQLException {
        if (score.getStudentId() == null) {
            throw new IllegalArgumentException("请选择学生");
        }
        if (score.getCourseId() == null) {
            throw new IllegalArgumentException("请选择课程");
        }
        if (score.getScore() != null && !ValidationUtil.isValidScore(score.getScore())) {
            throw new IllegalArgumentException("成绩必须在0-100之间");
        }
        
        // 验证学生是否存在
        if (studentDao.findById(score.getStudentId()) == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        
        // 验证课程是否存在
        if (courseDao.findById(score.getCourseId()) == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // 检查是否已存在相同的成绩记录
        String examType = score.getExamType() != null ? score.getExamType() : "期末";
        if (isNew) {
            if (scoreDao.exists(score.getStudentId(), score.getCourseId(), examType)) {
                throw new IllegalArgumentException("该学生此课程的" + examType + "成绩已存在");
            }
        } else {
            if (scoreDao.exists(score.getStudentId(), score.getCourseId(), examType, score.getId())) {
                throw new IllegalArgumentException("该学生此课程的" + examType + "成绩已存在");
            }
        }
    }
    
    /**
     * 检查教师是否有权限修改成绩
     */
    public boolean canTeacherModifyScore(Integer teacherId, Integer scoreId) throws SQLException {
        Score score = scoreDao.findById(scoreId);
        if (score == null) return false;
        
        Course course = courseDao.findById(score.getCourseId());
        return course != null && teacherId.equals(course.getTeacherId());
    }
    
    /**
     * 获取课程平均分
     */
    public Double getCourseAverageScore(Integer courseId) throws SQLException {
        return scoreDao.getAverageScoreByCourseId(courseId);
    }
    
    /**
     * 获取课程最高分
     */
    public Double getCourseMaxScore(Integer courseId) throws SQLException {
        return scoreDao.getMaxScoreByCourseId(courseId);
    }
    
    /**
     * 获取课程最低分
     */
    public Double getCourseMinScore(Integer courseId) throws SQLException {
        return scoreDao.getMinScoreByCourseId(courseId);
    }
    
    /**
     * 获取课程成绩分布
     */
    public Map<String, Integer> getCourseScoreDistribution(Integer courseId) throws SQLException {
        return scoreDao.getScoreDistributionByCourseId(courseId);
    }
    
    /**
     * 获取班级各科平均分
     */
    public List<Object[]> getClassCourseAverages(Integer classId) throws SQLException {
        return scoreDao.getClassCourseAverages(classId);
    }
    
    /**
     * 计算学生GPA
     */
    public double calculateStudentGPA(Integer studentId) throws SQLException {
        List<Score> scores = scoreDao.findByStudentId(studentId);
        if (scores.isEmpty()) return 0.0;
        
        double totalPoints = 0;
        double totalCredits = 0;
        
        for (Score score : scores) {
            if (score.getScore() != null && score.getCredit() != null) {
                totalPoints += score.getGradePoint() * score.getCredit();
                totalCredits += score.getCredit();
            }
        }
        
        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
}