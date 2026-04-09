package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.ProjectApplicationsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectApplicationsPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/project-applications/*")
public class ProjectApplicationsController extends BaseController {

    ProjectApplicationsServiceImpl projectApplicationsServiceImpl = new ProjectApplicationsServiceImpl();

    /**
     * 新增课程申请
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String pId = getStringParam(jsonNode, "pId");

        if (!validateRequiredParams(uId, pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectApplicationsServiceImpl.insert(uId, pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 更新课程申请（仅审核中状态可编辑）
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String paId = getStringParam(jsonNode, "paId");
        String uId = getStringParam(jsonNode, "uId");
        String pId = getStringParam(jsonNode, "pId");

        if (!validateRequiredParams(paId, uId, pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectApplicationsServiceImpl.update(paId, uId, pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除课程申请（仅审核中状态可删除）
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String paId = getStringParam(jsonNode, "paId");

        if (!validateRequiredParams(paId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectApplicationsServiceImpl.delete(paId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 查询单个课程申请
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String paId = getStringParam(jsonNode, "paId");

        if (!validateRequiredParams(paId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<ProjectApplicationsPageVo> result = projectApplicationsServiceImpl.find(paId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 分页查询课程申请
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String paId = getStringParam(jsonNode, "paId");
        String uId = getStringParam(jsonNode, "uId");
        String pId = getStringParam(jsonNode, "pId");
        String paStatus = getStringParam(jsonNode, "paStatus");

        if (!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<ProjectApplicationsPageVo>>> result = projectApplicationsServiceImpl.findAll(pageIndex, pageSize, paId, uId, pId, paStatus);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 审核课程申请
     * paStatus: 2-通过, 3-不通过
     */
    protected void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String paId = getStringParam(jsonNode, "paId");
        String paStatus = getStringParam(jsonNode, "paStatus");

        if (!validateRequiredParams(paId, paStatus)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            // 验证状态值是否合法
            if (!paStatus.equals("2") && !paStatus.equals("3")) {
                writeJsonResponse(resp, Result.error("状态值不合法，只能为2(通过)或3(不通过)"));
                return;
            }
            
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectApplicationsServiceImpl.approve(paId, paStatus);
            writeJsonResponse(resp, result);
        }
    }
}