package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.AnnouncementsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.AnnouncementsPageVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/announcements/*")
public class AnnouncementsController extends BaseController {

    AnnouncementsServiceImpl announcementsServiceImpl = new AnnouncementsServiceImpl();

    /**
     * 分页查询所有公告
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String aId = getStringParam(jsonNode, "aId");
        String aTitle = getStringParam(jsonNode, "aTitle");
        String uId = getStringParam(jsonNode, "uId");
        String aTarget = getStringParam(jsonNode, "aTarget");
        String uName = getStringParam(jsonNode, "uName");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<AnnouncementsPageVo>>> result = announcementsServiceImpl.findAll(pageIndex, pageSize, aId, aTitle, uId, aTarget, uName);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询公告
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String aId = getStringParam(jsonNode, "aId");
        
        if(!validateRequiredParams(aId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<AnnouncementsPageVo> result = announcementsServiceImpl.find(aId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 新增公告
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String aTitle = getStringParam(jsonNode, "aTitle");
        String aContent = getStringParam(jsonNode, "aContent");
        String uId = (String) req.getAttribute("uId");
        String aTarget = getStringParam(jsonNode, "aTarget");
        
        if(!validateRequiredParams(aTitle, aContent, uId, aTarget)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = announcementsServiceImpl.insert(aTitle, aContent, uId, aTarget);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 更新公告
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String aId = getStringParam(jsonNode, "aId");
        String aTitle = getStringParam(jsonNode, "aTitle");
        String aContent = getStringParam(jsonNode, "aContent");
        String aTarget = getStringParam(jsonNode, "aTarget");
        
        if(!validateRequiredParams(aId, aTitle, aContent, aTarget)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = announcementsServiceImpl.update(aId, aTitle, aContent, aTarget);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除公告
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String aId = getStringParam(jsonNode, "aId");
        
        if(!validateRequiredParams(aId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = announcementsServiceImpl.delete(aId);
            writeJsonResponse(resp, result);
        }
    }
}