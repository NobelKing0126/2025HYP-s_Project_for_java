package dao;

import entity.User;
import java.sql.*;
import java.util.List;

/**
 * 用户数据访问类
 */
public class UserDao extends BaseDao<User> {
    
    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setRelatedId(rs.getObject("related_id") != null ? rs.getInt("related_id") : null);
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setStatus(rs.getInt("status"));
        return user;
    }
    
    /**
     * 根据用户名和密码查询用户（登录验证）
     */
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ? AND status = 1";
        return queryOne(sql, username, password);
    }
    
    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM tb_user WHERE username = ?";
        return queryOne(sql, username);
    }
    
    /**
     * 查询所有用户
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM tb_user ORDER BY create_time DESC";
        return queryList(sql);
    }
    
    /**
     * 根据角色查询用户
     */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM tb_user WHERE role = ? ORDER BY create_time DESC";
        return queryList(sql, role);
    }
    
    /**
     * 添加用户
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO tb_user (username, password, role, related_id, status) VALUES (?, ?, ?, ?, ?)";
        return executeInsert(sql, user.getUsername(), user.getPassword(), 
                user.getRole(), user.getRelatedId(), user.getStatus() != null ? user.getStatus() : 1);
    }
    
    /**
     * 更新用户
     */
    public int update(User user) throws SQLException {
        String sql = "UPDATE tb_user SET password = ?, role = ?, related_id = ?, status = ? WHERE id = ?";
        return executeUpdate(sql, user.getPassword(), user.getRole(), 
                user.getRelatedId(), user.getStatus(), user.getId());
    }
    
    /**
     * 修改密码
     */
    public int updatePassword(Integer userId, String newPassword) throws SQLException {
        String sql = "UPDATE tb_user SET password = ? WHERE id = ?";
        return executeUpdate(sql, newPassword, userId);
    }
    
    /**
     * 删除用户
     */
    public int delete(Integer id) throws SQLException {
        String sql = "DELETE FROM tb_user WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE username = ?";
        return queryCount(sql, username) > 0;
    }
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    public boolean existsByUsername(String username, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE username = ? AND id != ?";
        return queryCount(sql, username, excludeId) > 0;
    }
}