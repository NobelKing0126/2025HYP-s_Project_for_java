package service;

import dao.CourseDao;
import dao.TeacherDao;
import dao.ScoreDao;
import entity.Course;
import entity.Teacher;

import java.sql.SQLException;
import java.util.List;

/**
 * 课程业务服务类
 */
public class CourseService {
    
    private CourseDao courseDao = new CourseDao();
    private TeacherDao teacherDao = new TeacherDao();
    private ScoreDao scoreDao = new ScoreDao();
    
    /**
     * 查询所有课程
     */
    public List<Course> findAll() throws SQLException {
        return courseDao.findAll();
    }
    
    /**
     * 根据ID查询课程
     */
    public Course findById(Integer id) throws SQLException {
        return courseDao.findById(id);
    }
    
    /**
     * 根据课程编号查询
     */
    public Course findByCourseNo(String courseNo) throws SQLException {
        return courseDao.findByCourseNo(courseNo);
    }
    
    /**
     * 根据教师ID查询课程
     */
    public List<Course> findByTeacherId(Integer teacherId) throws SQLException {
        return courseDao.findByTeacherId(teacherId);
    }
    
    /**
     * 多条件搜索课程
     */
    public List<Course> search(String courseNo, String courseName, Integer teacherId,
                               String semester, String courseType) throws SQLException {
        return courseDao.search(courseNo, courseName, teacherId, semester, courseType);
    }
    
    /**
     * 添加课程
     */
    public int addCourse(Course course) throws SQLException {
        // 数据验证
        validateCourse(course, true);
        
        return courseDao.insert(course);
    }
    
    /**
     * 更新课程
     */
    public boolean updateCourse(Course course) throws SQLException {
        // 数据验证
        validateCourse(course, false);
        
        return courseDao.update(course) > 0;
    }
    
    /**
     * 删除课程
     */
    public boolean deleteCourse(Integer id) throws SQLException {
        // 检查是否有成绩记录
        if (courseDao.hasScores(id)) {
            throw new IllegalArgumentException("该课程已有成绩记录，无法删除");
        }
        
        return courseDao.delete(id) > 0;
    }
    
    /**
     * 验证课程数据
     */
    private void validateCourse(Course course, boolean isNew) throws SQLException {
        if (course.getCourseNo() == null || course.getCourseNo().trim().isEmpty()) {
            throw new IllegalArgumentException("课程编号不能为空");
        }
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        
        // 验证学分范围
        if (course.getCredit() != null) {
            if (course.getCredit() < 0 || course.getCredit() > 10) {
                throw new IllegalArgumentException("学分必须在0-10之间");
            }
        }
        
        // 验证学时
        if (course.getHours() != null) {
            if (course.getHours() < 0 || course.getHours() > 200) {
                throw new IllegalArgumentException("学时必须在0-200之间");
            }
        }
        
        // 验证教师是否存在
        if (course.getTeacherId() != null) {
            Teacher teacher = teacherDao.findById(course.getTeacherId());
            if (teacher == null) {
                throw new IllegalArgumentException("指定的教师不存在");
            }
        }
        
        // 检查课程编号唯一性
        if (isNew) {
            if (courseDao.existsByCourseNo(course.getCourseNo())) {
                throw new IllegalArgumentException("课程编号已存在");
            }
        } else {
            if (courseDao.existsByCourseNo(course.getCourseNo(), course.getId())) {
                throw new IllegalArgumentException("课程编号已被其他课程使用");
            }
        }
    }
    
    /**
     * 获取所有学期列表
     */
    public List<String> getAllSemesters() throws SQLException {
        return courseDao.findAllSemesters();
    }
    
    /**
     * 检查课程是否有成绩记录
     */
    public boolean hasScores(Integer courseId) throws SQLException {
        return courseDao.hasScores(courseId);
    }
    
    /**
     * 获取课程统计信息
     */
    public CourseStats getCourseStats(Integer courseId) throws SQLException {
        CourseStats stats = new CourseStats();
        
        stats.setAverageScore(scoreDao.getAverageScoreByCourseId(courseId));
        stats.setMaxScore(scoreDao.getMaxScoreByCourseId(courseId));
        stats.setMinScore(scoreDao.getMinScoreByCourseId(courseId));
        stats.setScoreDistribution(scoreDao.getScoreDistributionByCourseId(courseId));
        
        return stats;
    }
    
    /**
     * 课程统计信息内部类
     */
    public static class CourseStats {
        private Double averageScore;
        private Double maxScore;
        private Double minScore;
        private java.util.Map<String, Integer> scoreDistribution;
        
        // Getters and Setters
        public Double getAverageScore() { return averageScore; }
        public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
        
        public Double getMaxScore() { return maxScore; }
        public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
        
        public Double getMinScore() { return minScore; }
        public void setMinScore(Double minScore) { this.minScore = minScore; }
        
        public java.util.Map<String, Integer> getScoreDistribution() { return scoreDistribution; }
        public void setScoreDistribution(java.util.Map<String, Integer> scoreDistribution) { 
            this.scoreDistribution = scoreDistribution; 
        }
    }
}