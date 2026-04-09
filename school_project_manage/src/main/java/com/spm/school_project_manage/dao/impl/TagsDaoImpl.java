package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.TagsDao;
import com.spm.school_project_manage.dto.TagsPageDto;
import com.spm.school_project_manage.utils.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TagsDaoImpl extends BaseDAO implements TagsDao {
    @Override
    public TagsPageDto findByName(String tName) {
        String sql = "select * from tags where tName = ? and valid = 1";
        try {
            return executeQueryBean(TagsPageDto.class, sql, tName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String tName) {
        String sql = "insert into tags (tName) values (?)";
        try {
            return executeInsert(sql, tName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TagsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select * from tags where valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        sql.append(" order by createdAt desc limit ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(TagsPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select count(*) from tags where valid = 1");

        List<Object> parameters = paramsPage(conditions, sql);

        try {
            return executeCountQuery(sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TagsPageDto find(String tId) {
        String sql = "select * from tags where tId = ? and valid = 1";
        try {
            return executeQueryBean(TagsPageDto.class, sql, tId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String tId, String tName) {
        String sql = "update tags set tName = ? where tId = ? and valid = 1";
        try {
            return executeUpdate(sql, tName, tId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String tId) {
        String sql = "update tags set valid = 0 where tId = ? and valid = 1";
        try {
            return executeUpdate(sql, tId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TagsPageDto> select() {
        StringBuilder sql = new StringBuilder("select * from tags where valid = 1");

        try {
            return executeQuery(TagsPageDto.class, sql.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}