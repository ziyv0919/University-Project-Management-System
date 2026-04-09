package com.spm.school_project_manage.service.impl;

import com.spm.school_project_manage.dao.impl.UsersDaoImpl;
import com.spm.school_project_manage.dto.UsersPageDto;
import com.spm.school_project_manage.service.UsersService;
import com.spm.school_project_manage.utils.EncryptUtils;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.UsersLoginVo;
import com.spm.school_project_manage.vo.UsersPageVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersServiceImpl extends EncryptUtils implements UsersService {

    UsersDaoImpl usersDaoImpl = new UsersDaoImpl();

    @Override
    public Result<UsersLoginVo> login(String uUsername, String uPassword) {
        UsersPageDto usersPageDto = usersDaoImpl.findByUsername(uUsername);

        if (usersPageDto == null) {
            return Result.error("用户名不存在或密码错误");
        }

        String encryptedPassword = encryptPassword(uPassword);

        if (!usersPageDto.getUPassword().equals(encryptedPassword)) {
            return Result.error("用户名不存在或密码错误");
        }

        return Result.success(usersPageDto, UsersLoginVo.class);
    }

    @Override
    public Result<Integer> register(String uUsername,String uPassword,String uName,String uRole) {

        // 检查用户名是否已存在
        UsersPageDto usersPageDto = usersDaoImpl.findByUsername(uUsername);
        if (usersPageDto != null) {
            return Result.error("用户已存在");
        }

        Integer result = usersDaoImpl.register(uUsername,encryptPassword(uPassword),uName,uRole);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("注册失败");
        }
    }

    @Override
    public Result<Integer> insert(String uUsername, String uPassword, String uName, String uRole) {
        // 检查用户名是否已存在
        UsersPageDto usersPageDto = usersDaoImpl.findByUsername(uUsername);
        if (usersPageDto != null) {
            return Result.error("用户已存在");
        }

        Integer result = usersDaoImpl.insert(uUsername,encryptPassword(uPassword),uName,uRole);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("注册失败");
        }
    }

    @Override
    public Result<PageResult<List<UsersPageVo>>> findAll(String pageIndex, String pageSize, String uId, String uRole, String uName, String uUsername, String uEmail) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("uId:=", uId);
        }
        if (uRole != null && !uRole.trim().isEmpty()) {
            conditions.put("uRole:LIKE", uRole);
        }
        if (uName != null && !uName.trim().isEmpty()) {
            conditions.put("uName:like",uName);
        }
        if (uUsername != null && !uUsername.trim().isEmpty()) {
            conditions.put("uUsername:like", uUsername);
        }
        if (uEmail != null && !uEmail.trim().isEmpty()) {
            conditions.put("uEmail:like", uEmail);
        }

        List<UsersPageDto> usersPageDtos = usersDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = usersDaoImpl.findAllCount(conditions);

        if (usersPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, usersPageDtos);
            Result<PageResult<List<UsersPageVo>>> result = Result.success(pageResult, UsersPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<UsersPageVo> find(String uId) {
        UsersPageDto usersPageDto = usersDaoImpl.find(uId);
        if (usersPageDto == null) {
            return Result.error("用户不存在");
        }
        return Result.success(usersPageDto, UsersPageVo.class);
    }

    @Override
    public Result<Integer> updatePassword(String uId, String uPassword, String uNewPassword) {
        // 检查用户名是否已存在
        UsersPageDto usersPageDto = usersDaoImpl.find(uId);
        if (usersPageDto == null) {
            return Result.error("用户不存在");
        }

        String encryptedPassword = encryptPassword(uPassword);

        if (!usersPageDto.getUPassword().equals(encryptedPassword)) {
            return Result.error("原密码错误");
        }

        Integer result = usersDaoImpl.updatePassword(uId,encryptPassword(uNewPassword));
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("修改密码失败");
        }
    }

    @Override
    public Result<Integer> update(String uId, String uName, String uRole, String uEmail, String uPhone) {
        // 检查用户名是否已存在
        UsersPageDto usersPageDto = usersDaoImpl.find(uId);
        if (usersPageDto == null) {
            return Result.error("用户不存在");
        }

        Integer result = usersDaoImpl.update(uId,uName,uRole,uEmail,uPhone);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("修改用户失败");
        }
    }

    @Override
    public Result<Integer> delete(String uId) {
        UsersPageDto usersPageDto = usersDaoImpl.find(uId);
        if (usersPageDto == null) {
            return Result.error("用户不存在");
        }

        Integer result = usersDaoImpl.delete(uId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    public Result<List<UsersPageVo>> remoteSelect(String searchText) {
        Map<String, Object> conditions = new HashMap<>();

        if (searchText != null && !searchText.isEmpty()) {
            conditions.put("uName:LIKE", searchText);
        }

        List<UsersPageDto> usersPageDtos = usersDaoImpl.findAll(1, 30, conditions);

        if (usersPageDtos != null) {
            return Result.success(usersPageDtos, UsersPageVo.class);
        }

        return Result.error("查询失败");
    }

    @Override
    public Result<Integer> avatar(String uId, String uAvatar) {
        // 检查用户名是否已存在
        UsersPageDto usersPageDto = usersDaoImpl.find(uId);
        if (usersPageDto == null) {
            return Result.error("用户不存在");
        }

        Integer result = usersDaoImpl.avatar(uId,uAvatar);

        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("头像修改失败");
        }
    }
}
