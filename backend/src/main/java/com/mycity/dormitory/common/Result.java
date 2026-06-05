package com.mycity.dormitory.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一 API 响应封装
 * 所有 Controller 都通过该类返回，保证前后端数据格式一致
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;    // 状态码：200=成功，500=失败
    private String message;  // 提示信息
    private T data;          // 返回的数据

    /** 无数据返回的成功响应 */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /** 带数据返回的成功响应 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /** 失败响应，传入错误信息 */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
