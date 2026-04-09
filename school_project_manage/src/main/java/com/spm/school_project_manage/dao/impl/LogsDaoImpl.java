package com.spm.school_project_manage.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.LogsDao;
import com.spm.school_project_manage.dto.LogsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

/**
 * 日志数据访问实现类
 */
public class LogsDaoImpl extends BaseDAO implements LogsDao {

    @Override
    public List<LogsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT l.*, u.uName FROM logs l ");
        sql.append("LEFT JOIN users u ON l.uId = u.uId ");
        sql.append("WHERE l.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" ORDER BY l.createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(LogsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM logs l ");
        sql.append("LEFT JOIN users u ON l.uId = u.uId ");
        sql.append("WHERE l.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LogsPageDto find(String logId) {
        StringBuilder sql = new StringBuilder("SELECT l.*, u.uName FROM logs l ");
        sql.append("LEFT JOIN users u ON l.uId = u.uId ");
        sql.append("WHERE l.logId = ? AND l.valid = 1");

        try {
            return executeQueryBean(LogsPageDto.class, sql.toString(), logId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insertLog(String uId, String requestUrl, String requestParams, String requestBody, String responseStatus, String responseBody, long processTime) {
        String sql = "INSERT INTO logs (uId, requestUrl, requestParams, requestBody, responseStatus, responseData, dealTime) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            return executeInsert(sql,
                    uId, requestUrl, requestParams, requestBody, responseStatus, responseBody, processTime);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}