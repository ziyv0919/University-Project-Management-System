package com.spm.school_project_manage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spm.school_project_manage.utils.JwtUtils;
import com.spm.school_project_manage.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenInterceptor {
    public static boolean validateToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error(401,"未登录或token已过期")));
                return false;
            }

            String token = authorization.substring(7);
            String uId = JwtUtils.getEmployeeIdFromToken(token);
            if (uId == null) {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error(401,"token无效或已过期")));
                return false;
            }

            // 将员工ID存储到request中，供其他接口使用
            request.setAttribute("uId", uId);
            return true;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error(401,"token格式错误，请确保token格式正确")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error(401,"token验证失败")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}