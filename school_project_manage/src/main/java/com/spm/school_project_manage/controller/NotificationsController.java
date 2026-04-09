package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.NotificationsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.NotificationsPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/notifications/*")
public class NotificationsController extends BaseController {

    NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();

    /**
     * 分页查询所有通知
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String nId = getStringParam(jsonNode, "nId");
        String uId = getStringParam(jsonNode, "uId");
        String nType = getStringParam(jsonNode, "nType");
        String nTitle = getStringParam(jsonNode, "nTitle");
        String nIsRead = getStringParam(jsonNode, "nIsRead");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<NotificationsPageVo>>> result = notificationsServiceImpl.findAll(pageIndex, pageSize, nId, uId, nType, nTitle, nIsRead);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询通知
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String nId = getStringParam(jsonNode, "nId");
        
        if(!validateRequiredParams(nId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<NotificationsPageVo> result = notificationsServiceImpl.find(nId);
            writeJsonResponse(resp, result);
        }
    }
    
    /**
     * 根据用户ID查询通知
     */
    protected void findByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String nIsRead = getStringParam(jsonNode, "nIsRead");
        String nTitle = getStringParam(jsonNode, "nTitle");
        
        if(!validateRequiredParams(uId, pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<NotificationsPageVo>>> result = notificationsServiceImpl.findByUserId(uId, pageIndex, pageSize, nIsRead, nTitle);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 新增通知
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String nTitle = getStringParam(jsonNode, "nTitle");
        String nContent = getStringParam(jsonNode, "nContent");
        String nType = getStringParam(jsonNode, "nType");
        
        if(!validateRequiredParams(uId, nTitle, nContent, nType)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = notificationsServiceImpl.insert(uId, nTitle, nContent, nType);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 标记通知为已读
     */
    protected void markAsRead(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String nId = getStringParam(jsonNode, "nId");
        
        if(!validateRequiredParams(nId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = notificationsServiceImpl.markAsRead(nId);
            writeJsonResponse(resp, result);
        }
    }
    
    /**
     * 标记用户所有通知为已读
     */
    protected void markAllAsRead(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        
        if(!validateRequiredParams(uId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = notificationsServiceImpl.markAllAsRead(uId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除通知
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String nId = getStringParam(jsonNode, "nId");
        
        if(!validateRequiredParams(nId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = notificationsServiceImpl.delete(nId);
            writeJsonResponse(resp, result);
        }
    }
}