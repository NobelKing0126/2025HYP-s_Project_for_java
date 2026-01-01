package entity;

import java.util.Date;

/**
 * 教师实体类
 */
public class Teacher {
    private Integer id;
    private String teacherNo;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String department;
    private String title;
    private Date createTime;
    
    public Teacher() {}
    
    public Teacher(String teacherNo, String name) {
        this.teacherNo = teacherNo;
        this.name = name;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTeacherNo() {
        return teacherNo;
    }
    
    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return name + " (" + teacherNo + ")";
    }
}