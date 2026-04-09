package com.spm.school_project_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.NotificationsDaoImpl;
import com.spm.school_project_manage.dto.NotificationsPageDto;
import com.spm.school_project_manage.service.NotificationsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.NotificationsPageVo;

public class NotificationsServiceImpl implements NotificationsService {

    NotificationsDaoImpl notificationsDaoImpl = new NotificationsDaoImpl();

    @Override
    public Result<PageResult<List<NotificationsPageVo>>> findAll(String pageIndex, String pageSize, String nId, String uId, String nType, String nTitle, String nIsRead) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (nId != null && !nId.trim().isEmpty()) {
            conditions.put("n.nId:=", nId);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("u.uId:=", uId);
        }
        if (nType != null && !nType.trim().isEmpty()) {
            conditions.put("n.nType:=", nType);
        }
        if (nTitle != null && !nTitle.trim().isEmpty()) {
            conditions.put("n.nTitle:LIKE", nTitle);
        }
        if (nIsRead != null && !nIsRead.trim().isEmpty()) {
            conditions.put("n.nIsRead:=", nIsRead);
        }

        List<NotificationsPageDto> notificationsPageDtos = notificationsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = notificationsDaoImpl.findAllCount(conditions);

        if (notificationsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, notificationsPageDtos);
            Result<PageResult<List<NotificationsPageVo>>> result = Result.success(pageResult, NotificationsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<NotificationsPageVo> find(String nId) {
        NotificationsPageDto notificationsPageDto = notificationsDaoImpl.find(nId);
        if (notificationsPageDto == null) {
            return Result.error("通知不存在");
        }
        return Result.success(notificationsPageDto, NotificationsPageVo.class);
    }

    @Override
    public Result<PageResult<List<NotificationsPageVo>>> findByUserId(String uId, String pageIndex, String pageSize, String nIsRead, String nTitle) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);
        Map<String, Object> conditions = new HashMap<>();
        if (nIsRead != null && !nIsRead.trim().isEmpty()) {
            conditions.put("n.nIsRead:=", nIsRead);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("u.uId:=", uId);
        }
        if (nTitle != null && !nTitle.trim().isEmpty()) {
            conditions.put("n.nTitle:LIKE", nTitle);
        }

        List<NotificationsPageDto> notificationsPageDtos = notificationsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = notificationsDaoImpl.findAllCount(conditions);

        if (notificationsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, notificationsPageDtos);
            Result<PageResult<List<NotificationsPageVo>>> result = Result.success(pageResult, NotificationsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<Integer> insert(String uId, String nTitle, String nContent, String nType) {
        Integer result = notificationsDaoImpl.insert(uId, nTitle, nContent, nType);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("新增通知失败");
        }
    }

    @Override
    public Result<Integer> markAsRead(String nId) {
        NotificationsPageDto notificationsPageDto = notificationsDaoImpl.find(nId);
        if (notificationsPageDto == null) {
            return Result.error("通知不存在");
        }

        Integer result = notificationsDaoImpl.markAsRead(nId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("标记已读失败");
        }
    }

    @Override
    public Result<Integer> markAllAsRead(String uId) {
        Integer result = notificationsDaoImpl.markAllAsRead(uId);
        return Result.success(result);
    }

    @Override
    public Result<Integer> delete(String nId) {
        NotificationsPageDto notificationsPageDto = notificationsDaoImpl.find(nId);
        if (notificationsPageDto == null) {
            return Result.error("通知不存在");
        }

        Integer result = notificationsDaoImpl.delete(nId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }
}