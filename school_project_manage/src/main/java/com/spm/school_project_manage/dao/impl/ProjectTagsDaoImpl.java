package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.ProjectTagsDao;
import com.spm.school_project_manage.dto.ProjectTagsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectTagsDaoImpl extends BaseDAO implements ProjectTagsDao {

    @Override
    public Integer insert(String pId, String tId) {
        String sql = "insert into project_tags (pId, tId) values (?, ?)";
        try {
            return executeInsert(sql, pId, tId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer batchInsert(String pId, List<String> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return 0;
        }

        try {
            // 开始事务
            beginTransaction();
            int count = 0;

            for (String tId : tagIds) {
                // 检查是否已存在该关联
                ProjectTagsPageDto existing = findByProjectAndTag(pId, tId);
                if (existing == null) {
                    String sql = "insert into project_tags (pId, tId) values (?, ?)";
                    count += executeInsert(sql, pId, tId);
                }
            }

            // 提交事务
            commitTransaction();
            return count;
        } catch (SQLException e) {
            try {
                // 回滚事务
                rollbackTransaction();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String ptId) {
        String sql = "update project_tags set valid = 0 where ptId = ? and valid = 1";
        try {
            return executeUpdate(sql, ptId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer deleteByProjectId(String pId) {
        String sql = "update project_tags set valid = 0 where pId = ? and valid = 1";
        try {
            return executeUpdate(sql, pId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProjectTagsPageDto> findByProjectId(String pId) {
        String sql = "select pt.*, t.tName from project_tags pt " +
                "join tags t on pt.tId = t.tId " +
                "where pt.pId = ? and pt.valid = 1 and t.valid = 1";
        try {
            return executeQuery(ProjectTagsPageDto.class, sql, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProjectsPageDto> findProjectsByTagId(String tId, int pageIndex, int pageSize) {
        String sql = "select p.* from projects p " +
                "join project_tags pt on p.pId = pt.pId " +
                "where pt.tId = ? and p.valid = 1 and pt.valid = 1 " +
                "order by p.createdAt desc limit ?, ?";
        try {
            return executeQuery(ProjectsPageDto.class, sql, tId, (pageIndex - 1) * pageSize, pageSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findProjectsCountByTagId(String tId) {
        String sql = "select count(*) from projects p " +
                "join project_tags pt on p.pId = pt.pId " +
                "where pt.tId = ? and p.valid = 1 and pt.valid = 1";
        try {
            return executeCountQuery(sql, tId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectTagsPageDto findByProjectAndTag(String pId, String tId) {
        String sql = "select * from project_tags where pId = ? and tId = ? and valid = 1";
        try {
            return executeQueryBean(ProjectTagsPageDto.class, sql, pId, tId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 回滚事务
     */
    public void rollbackTransaction() throws SQLException {
        try {
            super.rollbackTransaction();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}