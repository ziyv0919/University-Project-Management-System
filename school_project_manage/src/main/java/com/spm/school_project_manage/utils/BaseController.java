package com.spm.school_project_manage.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

public class BaseController extends HttpServlet {

    protected JsonNode parseRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(requestBody.toString());
    }

    protected void setupRequestAndResponse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
    }

    /**
     * 将对象转换为 JSON 字符串并写入响应。
     */
    protected void writeJsonResponse(HttpServletResponse resp, Object result) throws IOException {
        String jsonStr = JSONObject.fromObject(result).toString();
        resp.getWriter().write(jsonStr);
    }

    /**
     * 从JsonNode中提取字符串参数
     */
    protected String getStringParam(JsonNode jsonNode, String paramName) {
        return jsonNode.has(paramName) ? jsonNode.get(paramName).asText() : null;
    }

    protected boolean validateRequiredParams(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从JsonNode中提取字符串数组参数
     */
    protected List<String> getStringArrayParam(JsonNode jsonNode, String paramName) {
        List<String> result = new ArrayList<>();
        if (jsonNode.has(paramName)) {
            JsonNode arrayNode = jsonNode.get(paramName);
            if (arrayNode.isArray()) {
                for (JsonNode node : arrayNode) {
                    result.add(node.asText());
                }
            }
        }
        return result;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        String requestUrl = req.getRequestURI();
        String[] split = requestUrl.split("/");
        String methodName = split[split.length - 1];

        Class aClass = this.getClass();

        try {
            /*
             * getDeclaredMethod 获取类中被保护的所有方法
             * */
            Method declaredMethod = aClass.getDeclaredMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);

            /*
             * 暴力破解方法的访问修饰符的限制
             * */
            declaredMethod.setAccessible(true);

            declaredMethod.invoke(this,req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                writeJsonResponse(resp, Result.error("服务器内部错误"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
