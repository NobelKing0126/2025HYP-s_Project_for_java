package entity;

import java.util.Date;

/**
 * 班级实体类
 * 注意：由于class是Java关键字，使用Clazz作为类名
 */
public class Clazz {
    private Integer id;
    private String className;
    private String grade;
    private String major;
    private String department;
    private Date createTime;
    
    // 统计字段
    private Integer studentCount;
    
    public Clazz() {}
    
    public Clazz(String className, String grade, String major) {
        this.className = className;
        this.grade = grade;
        this.major = major;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getStudentCount() {
        return studentCount;
    }
    
    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
    
    @Override
    public String toString() {
        return className;
    }
}