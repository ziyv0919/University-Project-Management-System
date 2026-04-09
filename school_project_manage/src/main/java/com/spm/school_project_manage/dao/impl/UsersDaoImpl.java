package com.spm.school_project_manage.dao.impl;

import com.spm.school_project_manage.dao.UsersDao;
import com.spm.school_project_manage.dto.UsersPageDto;
import com.spm.school_project_manage.utils.BaseDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UsersDaoImpl extends BaseDAO implements UsersDao {
    @Override
    public UsersPageDto findByUsername(String uUsername) {
        String sql = "select * from users where uUsername = ? and valid = 1";
        try {
            return executeQueryBean(UsersPageDto.class, sql, uUsername);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer register(String uUsername, String uPassword, String uName,String uRole) {
        String sql = "insert into users (uUsername, uPassword, uName, uRole) values (?, ?, ?, ?)";
        try {
            return executeInsert(sql, uUsername, uPassword, uName, uRole);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer insert(String uUsername, String uPassword, String uName, String uRole) {
        String sql = "insert into users (uUsername, uPassword, uName, uRole) values (?, ?, ?, ?)";
        try {
            return executeInsert(sql, uUsername, uPassword, uName, uRole);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UsersPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select * from users where valid = 1");

        List<Object> parameters = paramsPage(conditions,sql);

        sql.append(" order by createdAt desc limit ?, ?");
        parameters.add((pageIndex - 1) * pageSize);
        parameters.add(pageSize);

        try {
            return executeQuery(UsersPageDto.class, sql.toString(), parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long findAllCount(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("select count(*) from users where valid = 1");

        List<Object> parameters = paramsPage(conditions,sql);

        try {
            return executeCountQuery(sql.toString(),parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UsersPageDto find(String uId) {
        String sql = "select * from users where uId = ? and valid = 1";
        try {
            return executeQueryBean(UsersPageDto.class, sql, uId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updatePassword(String uId, String uPassword) {
        String sql = "update users set uPassword = ? where uId = ? and valid = 1";
        try {
            return executeUpdate(sql, uPassword, uId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer update(String uId, String uName, String uRole, String uEmail, String uPhone) {
        String sql = "update users set uName = ?, uRole = ?, uEmail = ?, uPhone = ? where uId = ? and valid = 1";
        try {
            return executeUpdate(sql, uName, uRole, uEmail, uPhone, uId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer delete(String uId) {
        String sql = "update users set valid = 0 where uId = ? and valid = 1";
        try {
            return executeUpdate(sql , uId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer avatar(String uId, String uAvatar) {
        String sql = "update users set uAvatar = ? where uId = ? and valid = 1";
        try {
            return executeUpdate(sql, uAvatar, uId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
