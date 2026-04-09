package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.AnnouncementsDao;
import com.spm.school_project_manage.dto.AnnouncementsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AnnouncementsDaoImpl extends BaseDAO implements AnnouncementsDao {

    @Override
    public List<AnnouncementsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT a.*, u.uName FROM announcements a ");
        sql.append("LEFT JOIN users u ON a.uId = u.uId ");
        sql.append("WHERE a.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" ORDER BY a.createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(AnnouncementsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM announcements a ");
        sql.append("LEFT JOIN users u ON a.uId = u.uId ");
        sql.append("WHERE a.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AnnouncementsPageDto find(String aId) {
        StringBuilder sql = new StringBuilder("SELECT a.*, u.uName FROM announcements a ");
        sql.append("LEFT JOIN users u ON a.uId = u.uId ");
        sql.append("WHERE a.aId = ? AND a.valid = 1");

        try {
            return executeQueryBean(AnnouncementsPageDto.class, sql.toString(), aId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String aTitle, String aContent, String uId, String aTarget) {
        String sql = "INSERT INTO announcements (aTitle, aContent, uId, aTarget) VALUES (?, ?, ?, ?)"; 
        try {
            return executeInsert(sql, aTitle, aContent, uId, aTarget);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String aId, String aTitle, String aContent, String aTarget) {
        String sql = "UPDATE announcements SET aTitle = ?, aContent = ?, aTarget = ? WHERE aId = ? AND valid = 1";
        try {
            return executeUpdate(sql, aTitle, aContent, aTarget, aId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String aId) {
        String sql = "UPDATE announcements SET valid = 0 WHERE aId = ? AND valid = 1";
        try {
            return executeUpdate(sql, aId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}