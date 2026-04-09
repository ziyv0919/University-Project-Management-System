package com.spm.school_project_manage.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.ProjectsDao;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

public class ProjectsDaoImpl extends BaseDAO implements ProjectsDao {

    @Override
    public Integer insert(String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents) {
        String sql = "insert into projects (pTitle, pDescription, pDirection, pDifficulty, uId, pMaxStudents) values (?, ?, ?, ?, ?, ?)";
        try {
            return executeInsert(sql, pTitle, pDescription, pDirection, pDifficulty, uId, pMaxStudents);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProjectsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select p.*, u.uName from projects p left join users u on p.uId = u.uId where p.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" order by p.createdAt desc limit ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(ProjectsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select count(*) from projects p left join users u on p.uId = u.uId where p.valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectsPageDto find(String pId) {
        String sql = "select p.*, u.uName from projects p left join users u on p.uId = u.uId where p.pId = ? and p.valid = 1";
        try {
            return executeQueryBean(ProjectsPageDto.class, sql, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String pId, String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents) {
        String sql = "update projects set pTitle = ?, pDescription = ?, pDirection = ?, pDifficulty = ?, uId = ?, pMaxStudents = ? where pId = ? and valid = 1";
        try {
            return executeUpdate(sql, pTitle, pDescription, pDirection, pDifficulty, uId, pMaxStudents, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String pId) {
        String sql = "update projects set valid = 0 where pId = ? and valid = 1";
        try {
            return executeUpdate(sql, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateCover(String pId, String pCover) {
        String sql = "update projects set pCover = ? where pId = ? and valid = 1";
        try {
            return executeUpdate(sql, pCover, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}