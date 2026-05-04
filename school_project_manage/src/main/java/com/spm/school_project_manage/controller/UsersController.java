package com.spm.school_project_manage.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.spm.school_project_manage.service.impl.UsersServiceImpl;
import com.spm.school_project_manage.utils.BaseController;
import com.spm.school_project_manage.utils.JwtUtils;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.UsersLoginVo;
import com.spm.school_project_manage.vo.UsersPageVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users/*")
public class UsersController extends BaseController {

    UsersServiceImpl usersServiceImpl = new UsersServiceImpl();

    protected void login (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonNode jsonNode = parseRequestBody(req);
        String uUsername = getStringParam(jsonNode, "uUsername");
        String uPassword = getStringParam(jsonNode, "uPassword");

        if(!validateRequiredParams(uUsername, uPassword)){
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<UsersLoginVo> result = usersServiceImpl.login(uUsername, uPassword);
            if (result.getCode() == 200 && result.getData() != null) {
                String token = JwtUtils.generateToken(String.valueOf(result.getData().getUId()));
                result.getData().setToken(token);
            }
            writeJsonResponse(resp,result);
        }
    }

    protected void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uUsername = getStringParam(jsonNode, "uUsername");
        String uPassword = getStringParam(jsonNode, "uPassword");
        String uName = getStringParam(jsonNode, "uName");
        String uRole = getStringParam(jsonNode, "uRole");
        if(!validateRequiredParams(uUsername, uPassword, uName, uRole)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else if (!"2".equals(uRole) && !"3".equals(uRole)) {
            writeJsonResponse(resp, Result.error("角色参数错误，只能为2或3"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.register(uUsername,uPassword,uName,uRole);
            writeJsonResponse(resp,result);
        }
    }

    protected void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uUsername = getStringParam(jsonNode, "uUsername");
        String uPassword = getStringParam(jsonNode, "uPassword");
        String uName = getStringParam(jsonNode, "uName");
        String uRole = getStringParam(jsonNode, "uRole");
        if(!validateRequiredParams(uUsername, uPassword, uName, uRole)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        }else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.insert(uUsername,uPassword,uName,uRole);
            writeJsonResponse(resp,result);
        }
    }

    protected void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String pageIndex = getStringParam(jsonNode, "pageIndex");
        String pageSize = getStringParam(jsonNode, "pageSize");
        String uId = getStringParam(jsonNode, "uId");
        String uRole = getStringParam(jsonNode, "uRole");
        String uName = getStringParam(jsonNode, "uName");
        String uUsername = getStringParam(jsonNode, "uUsername");
        String uEmail = getStringParam(jsonNode, "uEmail");
        if(!validateRequiredParams(pageIndex, pageSize)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        }else {
            setupRequestAndResponse(req, resp);
            Result<PageResult<List<UsersPageVo>>> result = usersServiceImpl.findAll(pageIndex, pageSize, uId, uRole, uName, uUsername, uEmail);
            writeJsonResponse(resp,result);
        }
    }

    protected void find(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        if(!validateRequiredParams(uId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<UsersPageVo> result = usersServiceImpl.find(uId);
            writeJsonResponse(resp,result);
        }
    }

    protected void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String uPassword = getStringParam(jsonNode, "uPassword");
        String uNewPassword = getStringParam(jsonNode, "uNewPassword");
        if(!validateRequiredParams(uId, uPassword)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.updatePassword(uId, uPassword, uNewPassword);
            writeJsonResponse(resp,result);
        }
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String uName = getStringParam(jsonNode, "uName");
        String uRole = getStringParam(jsonNode, "uRole");
        String uEmail = getStringParam(jsonNode, "uEmail");
        String uPhone = getStringParam(jsonNode, "uPhone");
        if(!validateRequiredParams(uId, uName, uRole)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        }else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.update(uId, uName, uRole, uEmail, uPhone);
            writeJsonResponse(resp,result);
        }
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        if(!validateRequiredParams(uId)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.delete(uId);
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
            Result<List<UsersPageVo>> result = usersServiceImpl.remoteSelect(searchText);
            writeJsonResponse(resp, result);
        }
    }

    protected void avatar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        JsonNode jsonNode = parseRequestBody(req);
        String uId = getStringParam(jsonNode, "uId");
        String uAvatar = getStringParam(jsonNode, "uAvatar");
        if(!validateRequiredParams(uId, uAvatar)) {
            writeJsonResponse(resp, Result.error("请求参数错误"));
        } else {
            setupRequestAndResponse(req, resp);
            Result<Integer> result = usersServiceImpl.avatar(uId, uAvatar);
            writeJsonResponse(resp, result);
        }
    }
}
