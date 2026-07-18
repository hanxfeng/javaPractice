package com.example.javaPractice.common;

/**
 * 自定义业务异常
 */
// 已检查，书写正确
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
