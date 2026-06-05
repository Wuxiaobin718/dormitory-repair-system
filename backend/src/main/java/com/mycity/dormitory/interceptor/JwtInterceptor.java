package com.mycity.dormitory.interceptor;

import com.mycity.dormitory.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器：校验每个请求的登录状态
 * 登录/注册接口放行，其余 API 必须携带有效 token
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /** 无需登录的白名单路径 */
    private final String[] excludePaths = {
            "/api/user/register",
            "/api/user/login"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求（OPTIONS）直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 白名单路径直接放行
        String requestURI = request.getRequestURI();
        for (String excludePath : excludePaths) {
            if (requestURI.equals(excludePath)) {
                return true;
            }
        }

        // 检查请求头中是否有 Authorization: Bearer xxx
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return false;
        }

        // 提取 token 并校验是否有效
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token已过期或无效\"}");
            return false;
        }

        // 将用户信息存入 request，后续 Controller 可以通过 @RequestAttribute 获取
        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsername(token));

        return true;  // 放行
    }
}
