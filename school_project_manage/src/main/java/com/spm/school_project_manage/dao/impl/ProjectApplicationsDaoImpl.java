package com.spm.school_project_manage.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.ProjectApplicationsDao;
import com.spm.school_project_manage.dto.ProjectApplicationsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

public class ProjectApplicationsDaoImpl extends BaseDAO implements ProjectApplicationsDao {

    @Override
    public Integer insert(String uId, String pId) {
        String sql = "insert into project_applications (uId, pId) values (?, ?)";
        try {
            return executeInsert(sql, uId, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String paId, String uId, String pId) {
        String sql = "update project_applications set uId = ?, pId = ? where paId = ? and paStatus = 1 and valid = 1";
        try {
            return executeUpdate(sql, uId, pId, paId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String paId) {
        String sql = "update project_applications set valid = 0 where paId = ? and paStatus = 1 and valid = 1";
        try {
            return executeUpdate(sql, paId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectApplicationsPageDto find(String paId) {
        String sql = "select pa.*, u.uName, p.pTitle as pTitle from project_applications pa " +
                "left join users u on pa.uId = u.uId " +
                "left join projects p on pa.pId = p.pId " +
                "where pa.paId = ? and pa.valid = 1";
        try {
            return executeQueryBean(ProjectApplicationsPageDto.class, sql, paId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProjectApplicationsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select pa.*, u.uName, p.pTitle as pTitle from project_applications pa " +
                "left join users u on pa.uId = u.uId " +
                "left join projects p on pa.pId = p.pId " +
                "where pa.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" order by pa.createdAt desc limit ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(ProjectApplicationsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select count(*) from project_applications pa " +
                "left join users u on pa.uId = u.uId " +
                "left join projects p on pa.pId = p.pId " +
                "where pa.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateStatus(String paId, String paStatus) {
        String sql = "update project_applications set paStatus = ? where paId = ? and valid = 1";
        try {
            return executeUpdate(sql, paStatus, paId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long countApprovedStudents(String pId) {
        String sql = "select count(*) from project_applications where pId = ? and paStatus = 2 and valid = 1";
        try {
            return executeCountQuery(sql, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectApplicationsPageDto findByStudentAndProject(String uId, String pId) {
        String sql = "select pa.*, u.uName, p.pTitle as pTitle from project_applications pa " +
                "left join users u on pa.uId = u.uId " +
                "left join projects p on pa.pId = p.pId " +
                "where pa.uId = ? and pa.pId = ? and pa.valid = 1";
        try {
            return executeQueryBean(ProjectApplicationsPageDto.class, sql, uId, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}