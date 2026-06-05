package com.mycity.dormitory.exception;

import com.mycity.dormitory.common.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一捕获各类异常，返回标准化的 Result 响应，避免直接吐出错误堆栈
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 捕获参数校验失败异常（@Valid 触发的校验错误） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return Result.error(errors.toString());
    }

    /** 捕获业务逻辑异常（如密码错误、学号已存在等） */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.error(e.getMessage());
    }

    /** 捕获状态异常（如对未完成的报修单进行评价） */
    @ExceptionHandler(IllegalStateException.class)
    public Result<?> handleIllegalState(IllegalStateException e) {
        return Result.error(e.getMessage());
    }

    /** 兜底：捕获所有未被上述方法处理的异常，返回通用错误提示 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        e.printStackTrace();  // 打印堆栈便于开发排查
        return Result.error("服务器异常：" + e.getMessage());
    }
}
