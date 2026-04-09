package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.spm.school_project_manage.service.impl.ProjectTagsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;
import com.spm.school_project_manage.vo.TagsPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/project-tags/*")
public class ProjectTagsController extends BaseController {

    ProjectTagsServiceImpl projectTagsServiceImpl = new ProjectTagsServiceImpl();

    /**
     * 为课题分配标签
     */
    protected void assignTag(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        String tId = getStringParam(jsonNode, "tId");
        if(!validateRequiredParams(pId, tId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectTagsServiceImpl.assignTag(pId, tId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 批量为课题分配标签
     */
    protected void batchAssignTags(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        JsonNode tagIdsNode = jsonNode.get("tagIds");
        
        if(!validateRequiredParams(pId) || tagIdsNode == null || !tagIdsNode.isArray()) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
            return;
        }
        
        List<String> tagIds = new ArrayList<>();
        ArrayNode tagIdsArray = (ArrayNode) tagIdsNode;
        for (JsonNode tagId : tagIdsArray) {
            tagIds.add(tagId.asText());
        }
        
        setupRequestAndResponse(req, resp);
        Result<Integer> result = projectTagsServiceImpl.batchAssignTags(pId, tagIds);
        writeJsonResponse(resp, result);
    }

    /**
     * 删除课题标签关联
     */
    protected void removeTag(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String ptId = getStringParam(jsonNode, "ptId");
        if(!validateRequiredParams(ptId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectTagsServiceImpl.removeTag(ptId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 删除课题的所有标签
     */
    protected void removeAllTags(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        if(!validateRequiredParams(pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectTagsServiceImpl.removeAllTags(pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据课题ID查询标签
     */
    protected void findTagsByProjectId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        if(!validateRequiredParams(pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<List<TagsPageVo>> result = projectTagsServiceImpl.findTagsByProjectId(pId);
            writeJsonResponse(resp, result);
        }
    }

    /**
     * 根据标签ID查询课题
     */
    protected void findProjectsByTagId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String tId = getStringParam(jsonNode, "tId");
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        if(!validateRequiredParams(tId, pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<ProjectsPageVo>>> result = projectTagsServiceImpl.findProjectsByTagId(tId, pageIndex, pageSize);
            writeJsonResponse(resp, result);
        }
    }
}