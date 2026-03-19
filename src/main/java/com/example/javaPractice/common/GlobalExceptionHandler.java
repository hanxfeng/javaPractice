package com.example.javaPractice.common;

import com.example.javaPractice.Entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations={RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class )
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String message = split[2] + "已存在";
            return Result.error(message);
        }
        return Result.error("未知错误");
    }

    /**
     * 自定义处理业务异常的方法，这里直接返回异常的 message 即可
     * @param cx
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> customExceptionHandler(CustomException cx) {
        return Result.error(cx.getMessage());
    }
}
