package dao;

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础数据访问类
 * 封装通用的CRUD操作
 */
public abstract class BaseDao<T> {
    
    /**
     * 执行更新操作（INSERT, UPDATE, DELETE）
     * @param sql SQL语句
     * @param params 参数
     * @return 影响的行数
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        } finally {
            DBUtil.close(null, pstmt, conn);
        }
    }
    
    /**
     * 执行插入操作并返回生成的主键
     */
    protected int executeInsert(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameters(pstmt, params);
            pstmt.executeUpdate();
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询单个对象
     */
    protected T queryOne(String sql, Object... params) throws SQLException {
        List<T> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * 查询对象列表
     */
    protected List<T> queryList(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                T entity = mapRow(rs);
                if (entity != null) {
                    list.add(entity);
                }
            }
            return list;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询单个值
     */
    protected Object queryScalar(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParameters(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询记录数
     */
    protected int queryCount(String sql, Object... params) throws SQLException {
        Object result = queryScalar(sql, params);
        if (result instanceof Long) {
            return ((Long) result).intValue();
        } else if (result instanceof Integer) {
            return (Integer) result;
        }
        return 0;
    }
    
    /**
     * 设置PreparedStatement参数
     */
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }
    
    /**
     * 将ResultSet的一行映射为实体对象
     * 子类必须实现此方法
     */
    protected abstract T mapRow(ResultSet rs) throws SQLException;
}