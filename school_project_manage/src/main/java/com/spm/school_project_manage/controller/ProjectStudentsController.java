package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.ProjectStudentsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectStudentsPageVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/project-students/*")
public class ProjectStudentsController extends BaseController {

    ProjectStudentsServiceImpl projectStudentsServiceImpl = new ProjectStudentsServiceImpl();

    /**
     * 分页查询所有课程选课结果
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String psId = getStringParam(jsonNode, "psId");
        String uId = getStringParam(jsonNode, "uId");
        String pId = getStringParam(jsonNode, "pId");
        String uName = getStringParam(jsonNode, "uName");
        String pTitle = getStringParam(jsonNode, "pTitle");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<ProjectStudentsPageVo>>> result = projectStudentsServiceImpl.findAll(pageIndex, pageSize, psId, uId, pId, uName, pTitle);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询课程选课结果
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String psId = getStringParam(jsonNode, "psId");
        
        if(!validateRequiredParams(psId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<ProjectStudentsPageVo> result = projectStudentsServiceImpl.find(psId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 新增课程选课结果
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String pId = getStringParam(jsonNode, "pId");
        
        if(!validateRequiredParams(uId, pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectStudentsServiceImpl.insert(uId, pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除课程选课结果
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String psId = getStringParam(jsonNode, "psId");
        
        if(!validateRequiredParams(psId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectStudentsServiceImpl.delete(psId);
            writeJsonResponse(resp, result);
        }
    }
}