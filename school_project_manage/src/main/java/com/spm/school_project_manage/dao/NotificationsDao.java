package com.spm.school_project_manage.dao;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dto.NotificationsPageDto;

public interface NotificationsDao {
    /**
     * 分页查询所有通知
     */
    List<NotificationsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询总记录数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询通知
     */
    NotificationsPageDto find(String nId);
    
    /**
     * 根据用户ID查询通知
     */
    List<NotificationsPageDto> findByUserId(String uId, int pageIndex, int pageSize);
    
    /**
     * 根据用户ID查询通知总数
     */
    long findByUserIdCount(String uId);

    /**
     * 新增通知
     */
    Integer insert(String uId, String nTitle, String nContent, String nType);

    /**
     * 标记通知为已读
     */
    Integer markAsRead(String nId);
    
    /**
     * 标记用户所有通知为已读
     */
    Integer markAllAsRead(String uId);

    /**
     * 删除通知
     */
    Integer delete(String nId);
}