package com.spm.school_project_manage.service;

import java.util.List;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.NotificationsPageVo;

public interface NotificationsService {
    /**
     * 分页查询所有通知
     */
    Result<PageResult<List<NotificationsPageVo>>> findAll(String pageIndex, String pageSize, String nId, String uId, String nType, String nTitle, String nIsRead);

    /**
     * 根据ID查询通知
     */
    Result<NotificationsPageVo> find(String nId);
    
    /**
     * 根据用户ID查询通知
     */
    Result<PageResult<List<NotificationsPageVo>>> findByUserId(String uId, String pageIndex, String pageSize, String nIsRead, String nTitle);

    /**
     * 新增通知
     */
    Result<Integer> insert(String uId, String nTitle, String nContent, String nType);

    /**
     * 标记通知为已读
     */
    Result<Integer> markAsRead(String nId);
    
    /**
     * 标记用户所有通知为已读
     */
    Result<Integer> markAllAsRead(String uId);

    /**
     * 删除通知
     */
    Result<Integer> delete(String nId);
}