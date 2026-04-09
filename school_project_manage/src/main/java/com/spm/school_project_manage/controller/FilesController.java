package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.spm.school_project_manage.service.impl.FilesServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.FilesPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/files/*")
public class FilesController extends BaseController {

    FilesServiceImpl filesServiceImpl = new FilesServiceImpl();

    protected void batchInsert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        ArrayNode filesArray = (ArrayNode) jsonNode.get("files");
        
        if (filesArray == null || filesArray.size() == 0) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
            return;
        }
        
        List<Map<String, Object>> filesList = new ArrayList<>();
        for (JsonNode fileNode : filesArray) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("pId", getStringParam(fileNode, "pId"));
            fileMap.put("fFileUrl", getStringParam(fileNode, "fFileUrl"));
            fileMap.put("fOriginalName", getStringParam(fileNode, "fOriginalName"));
            fileMap.put("fSize", getStringParam(fileNode, "fSize"));
            filesList.add(fileMap);
        }
        
        setupRequestAndResponse(req, resp);
        Result<Integer> result = filesServiceImpl.batchInsert(filesList);
        writeJsonResponse(resp, result);
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String fId = getStringParam(jsonNode, "fId");
        
        if (!validateRequiredParams(fId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = filesServiceImpl.delete(fId);
            writeJsonResponse(resp, result);
        }
    }

    protected void batchDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        ArrayNode fIdsArray = (ArrayNode) jsonNode.get("fIds");
        
        if (fIdsArray == null || fIdsArray.size() == 0) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
            return;
        }
        
        List<String> fIds = new ArrayList<>();
        for (JsonNode fIdNode : fIdsArray) {
            fIds.add(fIdNode.asText());
        }
        
        setupRequestAndResponse(req, resp);
        Result<Integer> result = filesServiceImpl.batchDelete(fIds);
        writeJsonResponse(resp, result);
    }

    protected void findByProjectId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        
        if (!validateRequiredParams(pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<List<FilesPageVo>> result = filesServiceImpl.findByProjectId(pId);
            writeJsonResponse(resp, result);
        }
    }

    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String fId = getStringParam(jsonNode, "fId");
        String pId = getStringParam(jsonNode, "pId");
        String fOriginalName = getStringParam(jsonNode, "fOriginalName");
        
        if (!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<FilesPageVo>>> result = filesServiceImpl.findAll(pageIndex, pageSize, fId, pId, fOriginalName);
            writeJsonResponse(resp, result);
        }
    }

    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String fId = getStringParam(jsonNode, "fId");
        
        if (!validateRequiredParams(fId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<FilesPageVo> result = filesServiceImpl.find(fId);
            writeJsonResponse(resp, result);
        }
    }
}