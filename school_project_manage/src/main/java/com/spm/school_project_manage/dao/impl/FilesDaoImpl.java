package com.spm.school_project_manage.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.FilesDao;
import com.spm.school_project_manage.dto.FilesPageDto;
import com.spm.school_project_manage.utils.BaseDAO;

public class FilesDaoImpl extends BaseDAO implements FilesDao {

    @Override
    public Integer batchInsert(List<Map<String, Object>> filesList) {
        if (filesList == null || filesList.isEmpty()) {
            return 0;
        }
        
        String sql = "INSERT INTO files (pId, fFileUrl, fOriginalName, fSize) VALUES (?, ?, ?, ?)";
        List<Object[]> dataList = new ArrayList<>();
        
        for (Map<String, Object> file : filesList) {
            Object[] params = new Object[4];
            params[0] = file.get("pId");
            params[1] = file.get("fFileUrl");
            params[2] = file.get("fOriginalName");
            params[3] = file.get("fSize");
            dataList.add(params);
        }
        
        try {
            return executeBatchInsert(sql, dataList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String fId) {
        String sql = "UPDATE files SET valid = 0 WHERE fId = ? AND valid = 1";
        try {
            return executeUpdate(sql, fId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer batchDelete(List<String> fIds) {
        if (fIds == null || fIds.isEmpty()) {
            return 0;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE files SET valid = 0 WHERE valid = 1 AND fId IN (");
        for (int i = 0; i < fIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");
        
        try {
            return executeUpdate(sql.toString(), fIds.toArray());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FilesPageDto> findByProjectId(String pId) {
        String sql = "SELECT * FROM files WHERE pId = ? AND valid = 1 ORDER BY createdAt DESC";
        try {
            return executeQuery(FilesPageDto.class, sql, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FilesPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT f.*, p.pTitle FROM files f LEFT JOIN projects p ON f.pId = p.pId WHERE f.valid = 1 and p.valid = 1");
        
        List<Object> parameters = paramsPage(conditions, sql);
        
        sql.append(" ORDER BY createdAt DESC LIMIT ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);
        
        try {
            return executeQuery(FilesPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM files f LEFT JOIN projects p ON f.pId = p.pId WHERE f.valid = 1 and p.valid = 1");
        
        List<Object> parameters = paramsPage(conditions, sql);
        
        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FilesPageDto find(String fId) {
        String sql = "SELECT * FROM files WHERE fId = ? AND valid = 1";
        try {
            return executeQueryBean(FilesPageDto.class, sql, fId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}