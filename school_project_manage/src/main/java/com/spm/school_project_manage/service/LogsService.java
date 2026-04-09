package com.spm.school_project_manage.service;

import java.util.List;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.LogsPageVo;

public interface LogsService {
    /**
     * 分页查询所有日志
     */
    Result<PageResult<List<LogsPageVo>>> findAll(String pageIndex, String pageSize, String logId, String requestUrl, String uId, String uName);

    /**
     * 根据ID查询日志
     */
    Result<LogsPageVo> find(String logId);
}