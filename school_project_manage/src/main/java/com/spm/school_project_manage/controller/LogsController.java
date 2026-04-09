package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.LogsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.LogsPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/logs/*")
public class LogsController extends BaseController {

    LogsServiceImpl logsServiceImpl = new LogsServiceImpl();

    /**
     * 分页查询所有日志
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String logId = getStringParam(jsonNode, "logId");
        String requestUrl = getStringParam(jsonNode, "requestUrl");
        String uId = getStringParam(jsonNode, "uId");
        String uName = getStringParam(jsonNode, "uName");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<LogsPageVo>>> result = logsServiceImpl.findAll(pageIndex, pageSize, logId, requestUrl, uId, uName);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询日志
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String logId = getStringParam(jsonNode, "logId");
        
        if(!validateRequiredParams(logId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<LogsPageVo> result = logsServiceImpl.find(logId);
            writeJsonResponse(resp, result);
        }
    }
}