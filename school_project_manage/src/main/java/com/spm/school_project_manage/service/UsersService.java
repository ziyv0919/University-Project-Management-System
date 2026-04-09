package com.spm.school_project_manage.service;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.UsersLoginVo;
import com.spm.school_project_manage.vo.UsersPageVo;
import java.util.List;

public interface UsersService {
    Result<UsersLoginVo> login(String uUsername, String uPassword);

    Result<Integer> register(String uUsername,String uPassword,String uName,String uRole);

    Result<Integer> insert(String uUsername,String uPassword,String uName,String uRole);

    Result<PageResult<List<UsersPageVo>>> findAll(String pageIndex,String pageSize,String uId,String uRole,String uName,String uUsername,String uEmail);

    Result<UsersPageVo> find(String uId);

    Result<Integer> updatePassword(String uId,String uPassword,String newPassword);

    Result<Integer> update(String uId,String uName,String uRole,String uEmail,String uPhone);

    Result<Integer> delete(String uId);

    Result<List<UsersPageVo>> remoteSelect(String searchText);

    Result<Integer> avatar(String uId, String uAvatar);
}
