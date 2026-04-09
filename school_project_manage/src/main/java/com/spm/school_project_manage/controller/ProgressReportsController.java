package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.school_project_manage.service.impl.ProgressReportsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProgressReportsPageVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/progress-reports/*")
public class ProgressReportsController extends BaseController {

    ProgressReportsServiceImpl progressReportsServiceImpl = new ProgressReportsServiceImpl();

    /**
     * 分页查询所有课程阶段性进展
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String prId = getStringParam(jsonNode, "prId");
        String psId = getStringParam(jsonNode, "psId");
        String prTitle = getStringParam(jsonNode, "prTitle");
        String uName = getStringParam(jsonNode, "uName");
        String pTitle = getStringParam(jsonNode, "pTitle");
        String pId = getStringParam(jsonNode, "pId");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<ProgressReportsPageVo>>> result = progressReportsServiceImpl.findAll(pageIndex, pageSize, prId, psId, prTitle, uName, pTitle, pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询课程阶段性进展
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String prId = getStringParam(jsonNode, "prId");
        
        if(!validateRequiredParams(prId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<ProgressReportsPageVo> result = progressReportsServiceImpl.find(prId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 新增课程阶段性进展
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String psId = getStringParam(jsonNode, "psId");
        String prTitle = getStringParam(jsonNode, "prTitle");
        String prContent = getStringParam(jsonNode, "prContent");
        
        if(!validateRequiredParams(psId, prTitle, prContent)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = progressReportsServiceImpl.insert(psId, prTitle, prContent);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 更新课程阶段性进展
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String prId = getStringParam(jsonNode, "prId");
        String prTitle = getStringParam(jsonNode, "prTitle");
        String prContent = getStringParam(jsonNode, "prContent");
        
        if(!validateRequiredParams(prId, prTitle, prContent)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = progressReportsServiceImpl.update(prId, prTitle, prContent);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 教师评价课程阶段性进展
     */
    protected void evaluate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String prId = getStringParam(jsonNode, "prId");
        String prTeacherComment = getStringParam(jsonNode, "prTeacherComment");
        String prScore = getStringParam(jsonNode, "prScore");
        
        if(!validateRequiredParams(prId, prTeacherComment, prScore)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = progressReportsServiceImpl.evaluate(prId, prTeacherComment, prScore);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除课程阶段性进展
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String prId = getStringParam(jsonNode, "prId");
        
        if(!validateRequiredParams(prId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = progressReportsServiceImpl.delete(prId);
            writeJsonResponse(resp, result);
        }
    }
    
    /**
     * 批量新增课程阶段性进展
     */
    protected void batchInsert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        JsonNode reportsArray = jsonNode.get("reports");
        
        if (reportsArray == null || !reportsArray.isArray() || reportsArray.size() == 0) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
            return;
        }
        
        List<Map<String, String>> reportsList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        
        // 验证每条记录的必要参数
        boolean hasInvalidRecord = false;
        for (JsonNode report : reportsArray) {
            String psId = getStringParam(report, "psId");
            String prTitle = getStringParam(report, "prTitle");
            String prContent = getStringParam(report, "prContent");
            
            if (!validateRequiredParams(psId, prTitle, prContent)) {
                hasInvalidRecord = true;
                break;
            }
            
            Map<String, String> reportMap = mapper.convertValue(report, Map.class);
            reportsList.add(reportMap);
        }
        
        if (hasInvalidRecord) {
            writeJsonResponse(resp, Result.error("请求参数错误：存在无效的记录"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = progressReportsServiceImpl.batchInsert(reportsList);
            writeJsonResponse(resp, result);
        }
    }
}