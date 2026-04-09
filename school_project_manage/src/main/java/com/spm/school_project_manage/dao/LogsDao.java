package com.spm.school_project_manage.dao;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dto.LogsPageDto;

public interface LogsDao {
    /**
     * 分页查询所有日志
     */
    List<LogsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询总记录数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询日志
     */
    LogsPageDto find(String logId);
    
    /**
     * 插入日志
     */
    int insertLog(String uId, String requestUrl, String requestParams, String requestBody, String responseStatus, String responseBody, long processTime);
}