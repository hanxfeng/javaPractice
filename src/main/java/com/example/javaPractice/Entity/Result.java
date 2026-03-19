package com.example.javaPractice.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code; // 1 表示执行成功，0 表示执行失败
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(1, message, data);
    }
    public static <T> Result<T> success(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        return Result.error(0,message, null);
    }
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(0, message, data);
    }
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
