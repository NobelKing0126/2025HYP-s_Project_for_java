package entity;

import java.util.Date;

/**
 * 课程实体类
 */
public class Course {
    private Integer id;
    private String courseNo;
    private String courseName;
    private Double credit;
    private Integer hours;
    private Integer teacherId;
    private String semester;
    private String courseType; // 必修、选修
    private Date createTime;
    
    // 关联字段
    private String teacherName;
    
    public Course() {}
    
    public Course(String courseNo, String courseName, Double credit) {
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.credit = credit;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public Integer getHours() {
        return hours;
    }
    
    public void setHours(Integer hours) {
        this.hours = hours;
    }
    
    public Integer getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getCourseType() {
        return courseType;
    }
    
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    @Override
    public String toString() {
        return courseName + " (" + courseNo + ")";
    }
}