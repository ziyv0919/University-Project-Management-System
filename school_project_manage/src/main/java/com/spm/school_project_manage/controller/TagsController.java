package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.TagsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.TagsPageVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tags/*")
public class TagsController extends BaseController {

    TagsServiceImpl tagsServiceImpl = new TagsServiceImpl();

    /**
     * 新增标签
     */
    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String tName = getStringParam(jsonNode, "tName");
        if(!validateRequiredParams(tName)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = tagsServiceImpl.insert(tName);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 分页查询标签列表
     */
    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String tId = getStringParam(jsonNode, "tId");
        String tName = getStringParam(jsonNode, "tName");
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<TagsPageVo>>> result = tagsServiceImpl.findAll(pageIndex, pageSize, tId, tName);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据ID查询标签
     */
    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String tId = getStringParam(jsonNode, "tId");
        if(!validateRequiredParams(tId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<TagsPageVo> result = tagsServiceImpl.find(tId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 更新标签
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String tId = getStringParam(jsonNode, "tId");
        String tName = getStringParam(jsonNode, "tName");
        if(!validateRequiredParams(tId, tName)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = tagsServiceImpl.update(tId, tName);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除标签
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String tId = getStringParam(jsonNode, "tId");
        if(!validateRequiredParams(tId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = tagsServiceImpl.delete(tId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 远程检索标签
     */
    protected void remoteSelect(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String searchText = getStringParam(jsonNode, "searchText");
        if(!validateRequiredParams(searchText)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<List<TagsPageVo>> result = tagsServiceImpl.remoteSelect(searchText);
            writeJsonResponse(resp, result);
        }
    }

    protected void select(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            setupRequestAndResponse(req, resp);
            Result<List<TagsPageVo>> result = tagsServiceImpl.select();
            writeJsonResponse(resp, result);
    }
}