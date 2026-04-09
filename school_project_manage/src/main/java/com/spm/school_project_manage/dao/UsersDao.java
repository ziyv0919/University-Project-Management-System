package com.spm.school_project_manage.dao;

import com.spm.school_project_manage.dto.UsersPageDto;
import java.util.List;
import java.util.Map;

public interface UsersDao {
    UsersPageDto findByUsername(String uUsername);

    Integer register(String uUsername,String uPassword,String uName,String uRole);

    Integer insert(String uUsername,String uPassword,String uName,String uRole);

    List<UsersPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    long findAllCount(Map<String, Object> conditions);

    UsersPageDto find(String uId);

    Integer updatePassword(String uId,String uPassword);

    Integer update(String uId,String uName,String uRole,String uEmail,String uPhone);

    Integer delete(String uId);

    Integer avatar(String uId, String uAvatar);
}
