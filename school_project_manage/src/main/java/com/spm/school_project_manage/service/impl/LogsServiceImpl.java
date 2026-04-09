package com.spm.school_project_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.LogsDaoImpl;
import com.spm.school_project_manage.dto.LogsPageDto;
import com.spm.school_project_manage.service.LogsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.LogsPageVo;

public class LogsServiceImpl implements LogsService {

    LogsDaoImpl logsDaoImpl = new LogsDaoImpl();

    @Override
    public Result<PageResult<List<LogsPageVo>>> findAll(String pageIndex, String pageSize, String logId, String requestUrl, String uId, String uName) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (logId != null && !logId.trim().isEmpty()) {
            conditions.put("l.logId:=", logId);
        }
        if (requestUrl != null && !requestUrl.trim().isEmpty()) {
            conditions.put("l.requestUrl:LIKE", requestUrl);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("l.uId:=", uId);
        }
        if (uName != null && !uName.trim().isEmpty()) {
            conditions.put("u.uName:LIKE", uName);
        }

        List<LogsPageDto> logsPageDtos = logsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = logsDaoImpl.findAllCount(conditions);

        if (logsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, logsPageDtos);
            Result<PageResult<List<LogsPageVo>>> result = Result.success(pageResult, LogsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<LogsPageVo> find(String logId) {
        LogsPageDto logsPageDto = logsDaoImpl.find(logId);
        if (logsPageDto == null) {
            return Result.error("日志不存在");
        }
        return Result.success(logsPageDto, LogsPageVo.class);
    }
}