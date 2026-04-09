package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.ProjectStudentsDao;
import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProjectStudentsDaoImpl extends BaseDAO implements ProjectStudentsDao {
    @Override
    public List<ProjectStudentsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT ps.*, u.uName, p.pTitle FROM project_students ps ");
        sql.append("LEFT JOIN users u ON ps.uId = u.uId ");
        sql.append("LEFT JOIN projects p ON ps.pId = p.pId ");
        sql.append("WHERE ps.valid = 1 ");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" ORDER BY ps.createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(ProjectStudentsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM project_students ps ");
        sql.append("LEFT JOIN users u ON ps.uId = u.uId ");
        sql.append("LEFT JOIN projects p ON ps.pId = p.pId ");
        sql.append("WHERE ps.valid = 1 ");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectStudentsPageDto find(String psId) {
        String sql = "SELECT ps.*, u.uName, p.pTitle FROM project_students ps "
                + "LEFT JOIN users u ON ps.uId = u.uId "
                + "LEFT JOIN projects p ON ps.pId = p.pId "
                + "WHERE ps.psId = ? AND ps.valid = 1";
        try {
            return executeQueryBean(ProjectStudentsPageDto.class, sql, psId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectStudentsPageDto findByUIdAndPId(String uId, String pId) {
        String sql = "SELECT ps.*, u.uName, p.pTitle FROM project_students ps "
                + "LEFT JOIN users u ON ps.uId = u.uId "
                + "LEFT JOIN projects p ON ps.pId = p.pId "
                + "WHERE ps.uId = ? AND ps.pId = ? AND ps.valid = 1";
        try {
            return executeQueryBean(ProjectStudentsPageDto.class, sql, uId, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String uId, String pId) {
        String sql = "INSERT INTO project_students (uId, pId) VALUES (?, ?)"; 
        try {
            return executeInsert(sql, uId, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String psId) {
        String sql = "UPDATE project_students SET valid = 0 WHERE psId = ? AND valid = 1";
        try {
            return executeUpdate(sql, psId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}