package com.spm.school_project_manage.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.NotificationsDao;
import com.spm.school_project_manage.dto.NotificationsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

public class NotificationsDaoImpl extends BaseDAO implements NotificationsDao {
    @Override
    public List<NotificationsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT n.*, u.uName FROM notifications n "
                + "LEFT JOIN users u ON n.uId = u.uId WHERE n.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" ORDER BY n.createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(NotificationsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM notifications n LEFT JOIN users u ON n.uId = u.uId WHERE n.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotificationsPageDto find(String nId) {
        String sql = "SELECT n.*, u.uName FROM notifications n "
                + "LEFT JOIN users u ON n.uId = u.uId WHERE n.nId = ? AND n.valid = 1";
        try {
            return executeQueryBean(NotificationsPageDto.class, sql, nId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<NotificationsPageDto> findByUserId(String uId, int pageIndex, int pageSize) {
        String sql = "SELECT n.*, u.uName FROM notifications n "
                + "LEFT JOIN users u ON n.uId = u.uId WHERE n.uId = ? AND n.valid = 1 "
                + "ORDER BY n.createdAt DESC LIMIT ?, ?";
        try {
            return executeQuery(NotificationsPageDto.class, sql, uId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findByUserIdCount(String uId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE uId = ? AND valid = 1";
        try {
            return executeCountQuery(sql, uId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String uId, String nTitle, String nContent, String nType) {
        String sql = "INSERT INTO notifications (uId, nTitle, nContent, nType) VALUES (?, ?, ?, ?)";
        try {
            return executeInsert(sql, uId, nTitle, nContent, nType);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer markAsRead(String nId) {
        String sql = "UPDATE notifications SET nIsRead = 1 WHERE nId = ? AND valid = 1";
        try {
            return executeUpdate(sql, nId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer markAllAsRead(String uId) {
        String sql = "UPDATE notifications SET nIsRead = 1 WHERE uId = ? AND nIsRead = 0 AND valid = 1";
        try {
            return executeUpdate(sql, uId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String nId) {
        String sql = "UPDATE notifications SET valid = 0 WHERE nId = ? AND valid = 1";
        try {
            return executeUpdate(sql, nId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}