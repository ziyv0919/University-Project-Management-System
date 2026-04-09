package com.spm.school_project_manage.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.school_project_manage.dao.LogsDao;
import com.spm.school_project_manage.dao.impl.LogsDaoImpl;

import com.spm.school_project_manage.interceptor.TokenInterceptor;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {

    LogsDao logsDao = new LogsDaoImpl();

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }

    private String getRequestParams(HttpServletRequest req) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = req.getParameter(paramName);
            params.append(paramName).append("=").append(paramValue).append(",");
        }
        return params.length() > 0 ? params.substring(0, params.length() - 1) : "";
    }

    private int getResponseStatus(String responseBody, int defaultStatus) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            if (jsonNode.has("code")) {
                return jsonNode.get("code").asInt();
            }
        } catch (Exception e) {
            // 解析失败时不影响正常流程
        }
        return defaultStatus;
    }

    private void writeResponse(HttpServletResponse resp, ResponseWrapper responseWrapper) throws IOException {
        resp.getOutputStream().write(responseWrapper.getContentAsByteArray());
        resp.getOutputStream().flush();
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp,
                                ServletRequest servletRequest, ResponseWrapper responseWrapper,
                                FilterChain filterChain, String authorization) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(servletRequest, responseWrapper);
        } finally {
            String requestUrl = req.getRequestURI();
            String requestMethod = req.getMethod();
            String requestParams = getRequestParams(req);
            int responseStatus = responseWrapper.getStatus();
            String responseBody = responseWrapper.getContentAsString();
            responseStatus = getResponseStatus(responseBody, responseStatus);
            long processTime = System.currentTimeMillis() - startTime;
            String uId = (String) req.getAttribute("uId");
            // 将日志信息保存到数据库
            logsDao.insertLog(uId, requestUrl, requestParams, "", String.valueOf(responseStatus), responseBody, processTime);
            writeResponse(resp, responseWrapper);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        setCorsHeaders(resp);

        if ("OPTIONS".equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        ResponseWrapper responseWrapper = new ResponseWrapper(resp);
        String uri = req.getRequestURI();

        String authorization = req.getHeader("Authorization");



//         放行部分接口和静态资源
       Set<String> allowedPaths = Set.of(
               "/users/register",
               "/users/login",
               "/static/",
               "/users/findAll"
       );

       for (String path : allowedPaths) {
           if (uri.contains(path)) {
               processRequest(req, resp, servletRequest, responseWrapper, filterChain, authorization);
               return;
           }
       }

        // 验证token并获取员工ID
        if (!TokenInterceptor.validateToken(req, resp)) {
            return;
        }

        processRequest(req, resp, servletRequest, responseWrapper, filterChain, authorization);
    }
}

