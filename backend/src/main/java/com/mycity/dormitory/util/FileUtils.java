package com.mycity.dormitory.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * 文件上传工具类
 * 将图片保存到 static/uploads/ 目录，并将文件同步到 target/classes 确保运行时可访问
 */
public class FileUtils {

    /** 保存上传的图片，返回可访问的 URL 路径 */
    public static String saveFile(MultipartFile file) throws IOException {
        String UPLOAD_DIR = new File("src/main/resources/static/uploads/").getAbsolutePath();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 校验文件类型（仅允许图片）
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (!isImageFile(contentType, originalFilename)) {
            throw new IllegalArgumentException("不支持的文件类型，仅允许上传图片");
        }
        // 生成唯一文件名，防止覆盖
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String newFilename = UUID.randomUUID() + "." + fileExtension;
        File dest = new File(uploadDir, newFilename);
        file.transferTo(dest);

        // 同步到 target/classes 目录，保证打包后也能访问
        String targetDirPath = UPLOAD_DIR
                .replace("src/main/resources", "target/classes")
                .replace("src\\main\\resources", "target\\classes");
        File targetDir = new File(targetDirPath);
        targetDir.mkdirs();
        Files.copy(dest.toPath(), new File(targetDir, newFilename).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + newFilename;  // 返回 URL 路径
    }

    /** 校验是否为支持的图片格式 */
    private static boolean isImageFile(String contentType, String filename) {
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        String extension = StringUtils.getFilenameExtension(filename).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("png") || extension.equals("gif")
                || extension.equals("bmp");
    }

    /** 根据 URL 路径删除已上传的文件 */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        String UPLOAD_DIR = new File("src/main/resources/static/uploads/").getAbsolutePath();
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        File file = new File(UPLOAD_DIR, filename);
        return file.delete();
    }
}
