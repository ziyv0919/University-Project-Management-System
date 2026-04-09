package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.ProjectsServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/projects/*")
public class ProjectsController extends BaseController {

    ProjectsServiceImpl projectsServiceImpl = new ProjectsServiceImpl();

    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pTitle = getStringParam(jsonNode, "pTitle");
        String pDescription = getStringParam(jsonNode, "pDescription");
        String pDirection = getStringParam(jsonNode, "pDirection");
        String pDifficulty = getStringParam(jsonNode, "pDifficulty");
        String uId = getStringParam(jsonNode, "uId");
        String pMaxStudents = getStringParam(jsonNode, "pMaxStudents");
        List<String> tagIds = getStringArrayParam(jsonNode, "tagIds");
        
        if(!validateRequiredParams(pTitle, pDifficulty, uId, pMaxStudents)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectsServiceImpl.insert(
                pTitle, 
                pDescription, 
                pDirection, 
                Integer.parseInt(pDifficulty), 
                Integer.parseInt(uId), 
                Integer.parseInt(pMaxStudents),
                tagIds
            );

            
            writeJsonResponse(resp, result);
        }
    }

    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String pId = getStringParam(jsonNode, "pId");
        String pTitle = getStringParam(jsonNode, "pTitle");
        String pDirection = getStringParam(jsonNode, "pDirection");
        String pDifficulty = getStringParam(jsonNode, "pDifficulty");
        String uId = getStringParam(jsonNode, "uId");
        
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<ProjectsPageVo>>> result = projectsServiceImpl.findAll(
                pageIndex, pageSize, pId, pTitle, pDirection, pDifficulty, uId
            );
            writeJsonResponse(resp, result);
        }
    }

    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        
        if(!validateRequiredParams(pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<ProjectsPageVo> result = projectsServiceImpl.find(pId);
            writeJsonResponse(resp, result);
        }
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        String pTitle = getStringParam(jsonNode, "pTitle");
        String pDescription = getStringParam(jsonNode, "pDescription");
        String pDirection = getStringParam(jsonNode, "pDirection");
        String pDifficulty = getStringParam(jsonNode, "pDifficulty");
        String uId = getStringParam(jsonNode, "uId");
        String pMaxStudents = getStringParam(jsonNode, "pMaxStudents");
        List<String> tagIds = getStringArrayParam(jsonNode, "tagIds");
        
        if(!validateRequiredParams(pId, pTitle, pDifficulty, uId, pMaxStudents)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectsServiceImpl.update(
                pId,
                pTitle, 
                pDescription, 
                pDirection, 
                Integer.parseInt(pDifficulty), 
                Integer.parseInt(uId), 
                Integer.parseInt(pMaxStudents),
                    tagIds
            );
            writeJsonResponse(resp, result);
        }
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        
        if(!validateRequiredParams(pId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectsServiceImpl.delete(pId);
            writeJsonResponse(resp, result);
        }
    }

    protected void remoteSelect(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String searchText = getStringParam(jsonNode, "searchText");
        
        if(!validateRequiredParams(searchText)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<List<ProjectsPageVo>> result = projectsServiceImpl.remoteSelect(searchText);
            writeJsonResponse(resp, result);
        }
    }

    protected void cover(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pId = getStringParam(jsonNode, "pId");
        String pCover = getStringParam(jsonNode, "pCover");

        if(!validateRequiredParams(pId, pCover)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = projectsServiceImpl.updateCover(pId, pCover);
            writeJsonResponse(resp, result);
        }
    }
}
