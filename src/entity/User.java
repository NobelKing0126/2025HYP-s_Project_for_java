package entity;

import java.util.Date;

/**
 * 用户实体类
 */
public class User {
    private Integer id;
    private String username;
    private String password;
    private String role; // admin, teacher, student
    private Integer relatedId; // 关联的教师ID或学生ID
    private Date createTime;
    private Integer status;
    
    // 非数据库字段，用于显示
    private String roleName;
    private String realName;
    
    public User() {}
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Integer getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getRoleName() {
        if (roleName != null) return roleName;
        switch (role) {
            case "admin": return "管理员";
            case "teacher": return "教师";
            case "student": return "学生";
            default: return role;
        }
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    /**
     * 判断是否是管理员
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }
    
    /**
     * 判断是否是教师
     */
    public boolean isTeacher() {
        return "teacher".equals(role);
    }
    
    /**
     * 判断是否是学生
     */
    public boolean isStudent() {
        return "student".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", relatedId=" + relatedId +
                '}';
    }
}