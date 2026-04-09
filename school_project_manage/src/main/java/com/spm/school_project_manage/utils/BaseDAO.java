package com.spm.school_project_manage.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseDAO {

    // 当前线程事务连接（用于事务处理）
    private static ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    private static final Set<String> SUPPORTED_OPERATORS = new HashSet<>(Arrays.asList("=", ">", "<", ">=", "<=", "<>", "LIKE", "BETWEEN", "IN"));

    /**
     * 获取数据库连接
     * 如果当前线程有事务连接，返回事务连接；否则返回普通连接
     */
    private Connection getConnection() throws SQLException {
        Connection connection = threadConnection.get();
        if (connection == null) {
            connection = JDBCUtil.getConnection();
        }
        return connection;
    }

    /**
     * 开始事务
     */
    public void beginTransaction() throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Failed to obtain a valid database connection.");
        }
        connection.setAutoCommit(false);
        threadConnection.set(connection);
    }

    /**
     * 提交事务
     */
    public void commitTransaction() throws SQLException {
        Connection connection = threadConnection.get();
        if (connection != null) {
            connection.commit();
            threadConnection.remove();
        }
    }


    /**
     * 回滚事务
     */
    public void rollbackTransaction() throws SQLException {
        Connection connection = threadConnection.get();
        if (connection != null) {
            connection.rollback();
            threadConnection.remove();
        }
    }


    /**
     * 通用分页条件处理方法
     */

    protected List<Object> paramsPage(Map<String, Object> conditions, StringBuilder sql) {
        List<Object> parameters = new ArrayList<>();

        if (conditions != null && !conditions.isEmpty()) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                String column = entry.getKey();
                Object value = entry.getValue();

                // 默认操作符 =
                String operator = "=";

                // 检查操作符，支持 =, LIKE, BETWEEN, IN 等
                if (column.contains(":")) {
                    String[] parts = column.split(":");
                    column = parts[0];
                    operator = parts[1].toUpperCase();  // 取出操作符
                }

                // 如果操作符不支持，跳过该条件
                if (!SUPPORTED_OPERATORS.contains(operator)) {
                    continue;
                }

                // 处理不同类型的条件
                switch (operator) {
                    case "=":
                    case ">":
                    case "<":
                    case ">=":
                    case "<=":
                    case "<>":
                        sql.append(" and ").append(column).append(" ").append(operator).append(" ?");
                        parameters.add(value);
                        break;

                    case "LIKE":
                        sql.append(" and ").append(column).append(" like ?");
                        parameters.add("%" + value + "%");
                        break;

                    case "BETWEEN":
                        if (value instanceof List && ((List<?>) value).size() == 2) {
                            sql.append(" and ").append(column).append(" between ? and ?");
                            parameters.add(((List<?>) value).get(0));
                            parameters.add(((List<?>) value).get(1));
                        }
                        break;

                    case "IN":
                        if (value instanceof List) {
                            sql.append(" and ").append(column).append(" in (");
                            List<?> list = (List<?>) value;
                            for (int i = 0; i < list.size(); i++) {
                                if (i > 0) {
                                    sql.append(", ");
                                }
                                sql.append("?");
                                parameters.add(list.get(i));
                            }
                            sql.append(")");
                        }
                        break;

                    default:
                        // 如果是不支持的操作符，就跳过
                        break;
                }
            }
        }
        return parameters;
    }

    /**
     * 插入操作（返回生成的主键值）
     */
    public int executeInsert(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }

        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        int generatedId = -1;
        if (generatedKeys.next()) {
            generatedId = generatedKeys.getInt(1);
        }

        generatedKeys.close();
        preparedStatement.close();
        if (threadConnection.get() == null) {
            JDBCUtil.release();
        }
        return generatedId;
    }

    /**
     * 通用增删改方法
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }

        int row = preparedStatement.executeUpdate();
        preparedStatement.close();
        if (threadConnection.get() == null) {
            JDBCUtil.release();
        }
        return row;
    }

    /**
     * 通用查询方法（返回多条记录）
     */
    public <T> List<T> executeQuery(Class<T> clazz, String sql, Object... params) throws Exception {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T t = clazz.getDeclaredConstructor().newInstance();
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                String columnName = resultSetMetaData.getColumnLabel(i + 1);
                Object columnValue = resultSet.getObject(i + 1);
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(t, columnValue);
            }
            list.add(t);
        }

        resultSet.close();
        preparedStatement.close();
        if (threadConnection.get() == null) {
            JDBCUtil.release();
        }
        return list;
    }

    /**
     * 查询单条记录
     */
    public <T> T executeQueryBean(Class<T> clazz, String sql, Object... params) throws Exception {
        List<T> list = executeQuery(clazz, sql, params);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询记录总数
     */
    public long executeCountQuery(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        long count = 0;
        if (resultSet.next()) {
            count = resultSet.getLong(1);
        }

        resultSet.close();
        preparedStatement.close();
        if (threadConnection.get() == null) {
            JDBCUtil.release();
        }
        return count;
    }
    
    /**
     * 批量执行SQL插入操作
     * @param sql SQL语句
     * @param dataList 数据列表，每个元素是一行数据的参数数组
     * @return 成功插入的记录数
     * @throws SQLException 数据库异常
     */
    public int executeBatchInsert(String sql, List<Object[]> dataList) throws SQLException {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int result = 0;
        
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            
            for (Object[] params : dataList) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        preparedStatement.setObject(i + 1, params[i]);
                    }
                    preparedStatement.addBatch();
                }
            }
            
            int[] results = preparedStatement.executeBatch();
            connection.commit();
            
            for (int i : results) {
                result += i;
            }
            
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Failed to rollback transaction", ex);
                }
            }
            throw e;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null && threadConnection.get() == null) {
                JDBCUtil.release();
            }
        }
    }
}
