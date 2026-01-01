package entity;

import java.util.Date;

/**
 * 成绩实体类
 */
public class Score {
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private Double score;
    private String examType; // 平时、期中、期末
    private Date examDate;
    private Integer recorderId;
    private Date createTime;
    private Date updateTime;
    
    // 关联字段
    private String studentNo;
    private String studentName;
    private String className;
    private String courseNo;
    private String courseName;
    private Double credit;
    private String teacherName;
    
    public Score() {}
    
    public Score(Integer studentId, Integer courseId, Double score) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public Integer getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public String getExamType() {
        return examType;
    }
    
    public void setExamType(String examType) {
        this.examType = examType;
    }
    
    public Date getExamDate() {
        return examDate;
    }
    
    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }
    
    public Integer getRecorderId() {
        return recorderId;
    }
    
    public void setRecorderId(Integer recorderId) {
        this.recorderId = recorderId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getStudentNo() {
        return studentNo;
    }
    
    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getCourseNo() {
        return courseNo;
    }
    
    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public Double getCredit() {
        return credit;
    }
    
    public void setCredit(Double credit) {
        this.credit = credit;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    /**
     * 获取成绩等级
     */
    public String getGrade() {
        if (score == null) return "无";
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "不及格";
    }
    
    /**
     * 获取绩点
     */
    public Double getGradePoint() {
        if (score == null || score < 60) return 0.0;
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        return 1.0;
    }
    
    @Override
    public String toString() {
        return "Score{" +
                "studentName='" + studentName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", score=" + score +
                '}';
    }
}