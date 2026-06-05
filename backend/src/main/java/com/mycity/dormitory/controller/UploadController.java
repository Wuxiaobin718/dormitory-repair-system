package com.mycity.dormitory.controller;

import com.mycity.dormitory.common.Result;
import com.mycity.dormitory.util.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传 Controller：接收图片上传，返回可访问的 URL
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    /** POST /api/upload/image — 上传报修图片，返回图片 URL */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String path = FileUtils.saveFile(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", path);  // 前端可通过此 URL 直接展示图片
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
