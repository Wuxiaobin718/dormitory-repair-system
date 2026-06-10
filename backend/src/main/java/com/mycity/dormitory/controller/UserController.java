package com.mycity.dormitory.controller;

import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.dto.UserLoginDTO;
import com.mycity.dormitory.dto.UserRegisterDTO;
import com.mycity.dormitory.entity.User;
import com.mycity.dormitory.service.UserService;
import com.mycity.dormitory.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 Controller：注册、登录、获取/更新个人信息、修改密码
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;  // 用于获取拦截器存入的 userId

    /** POST /api/user/register — 用户注册 */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    /** POST /api/user/login — 用户登录，返回 token 和用户信息 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("name", user.getName());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());
        return Result.success(data);
    }

    /** GET /api/user/info — 获取当前登录用户的详细信息 */
    @GetMapping("/info")
    public Result<User> info() {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);  // 密码不返回给前端
        return Result.success(user);
    }

    /** PUT /api/user/profile — 更新个人资料（头像、手机号等） */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        User existing = userService.getById(userId);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        user.setId(userId);
        user.setPassword(null);       // 禁止通过此接口修改密码
        userService.updateById(user);
        user = userService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    /** POST /api/user/change-password — 当前用户修改密码（需旧密码验证） */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("旧密码和新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /** GET /api/user/search — 管理员按学号查询用户（用于重置密码时查找目标） */
    @GetMapping("/search")
    public Result<User> search(@RequestParam String username) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            return Result.error("无权限");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /** POST /api/user/reset-password — 管理员重置指定用户密码（无需旧密码） */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, Long> body) {
        Long adminId = (Long) request.getAttribute("userId");
        Long targetUserId = body.get("userId");
        if (targetUserId == null) {
            return Result.error("请指定要重置密码的用户");
        }
        // 默认重置密码为 123456
        userService.resetPassword(adminId, targetUserId, "123456");
        return Result.success();
    }
}
