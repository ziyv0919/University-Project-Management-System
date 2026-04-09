package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.ProgressReportsDao;
import com.spm.school_project_manage.dto.ProgressReportsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProgressReportsDaoImpl extends BaseDAO implements ProgressReportsDao {

    @Override
    public List<ProgressReportsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT pr.*, ps.uName, ps.pId, ps.pTitle, ps.uId FROM progress_reports pr ");
        sql.append("LEFT JOIN (SELECT ps.psId, u.uId, u.uName, p.pTitle, p.pId FROM project_students ps ");
        sql.append("LEFT JOIN users u ON ps.uId = u.uId ");
        sql.append("LEFT JOIN projects p ON ps.pId = p.pId) ps ON pr.psId = ps.psId ");
        sql.append("WHERE pr.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" ORDER BY pr.createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(ProgressReportsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM progress_reports pr ");
        sql.append("LEFT JOIN (SELECT ps.psId, p.pId, u.uId, u.uName, p.pTitle FROM project_students ps ");
        sql.append("LEFT JOIN users u ON ps.uId = u.uId ");
        sql.append("LEFT JOIN projects p ON ps.pId = p.pId) ps ON pr.psId = ps.psId ");
        sql.append("WHERE pr.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProgressReportsPageDto find(String prId) {
        StringBuilder sql = new StringBuilder("SELECT pr.*, ps.uName, ps.pTitle, ps.uId FROM progress_reports pr ");
        sql.append("LEFT JOIN (SELECT ps.psId, u.uId, u.uName, p.pTitle FROM project_students ps ");
        sql.append("LEFT JOIN users u ON ps.uId = u.uId ");
        sql.append("LEFT JOIN projects p ON ps.pId = p.pId) ps ON pr.psId = ps.psId ");
        sql.append("WHERE pr.prId = ? AND pr.valid = 1");

        try {
            return executeQueryBean(ProgressReportsPageDto.class, sql.toString(), prId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String psId, String prTitle, String prContent) {
        String sql = "INSERT INTO progress_reports (psId, prTitle, prContent) VALUES (?, ?, ?)"; 
        try {
            return executeInsert(sql, psId, prTitle, prContent);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String prId, String prTitle, String prContent) {
        String sql = "UPDATE progress_reports SET prTitle = ?, prContent = ? WHERE prId = ? AND valid = 1";
        try {
            return executeUpdate(sql, prTitle, prContent, prId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer evaluate(String prId, String prTeacherComment, int prScore) {
        String sql = "UPDATE progress_reports SET prTeacherComment = ?, prScore = ? WHERE prId = ? AND valid = 1";
        try {
            return executeUpdate(sql, prTeacherComment, prScore, prId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String prId) {
        String sql = "UPDATE progress_reports SET valid = 0 WHERE prId = ? AND valid = 1";
        try {
            return executeUpdate(sql, prId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}