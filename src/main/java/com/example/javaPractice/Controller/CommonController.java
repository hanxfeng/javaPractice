package com.example.javaPractice.Controller;

import com.example.javaPractice.Entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 用于文件的上传和下载
 */

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("reggie.path")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("upload")
    // 该参数名必须与前端的参数名保持一致
    // 在不进行主动设置的前提下，file会以 .temp 的形式被保村在一个临时文件夹中，运行完毕后被删除
    public Result<String> upload(MultipartFile file) throws IOException {
        // 文件名一般使用随机生成的uuid,如果使用上传时的文件名，可能出现被覆盖的情况
        // 随机生成uuid
        String s = UUID.randomUUID().toString();
        // 获取上传文件的后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 拼接文件名
        String fileName = basePath+suffix;

        // 判断当前文件保存目录是否存在
        File dir = new File(basePath);
        if(!dir.exists()) {
            // 目录不存在，需要创建
            dir.mkdirs();
        }
        file.transferTo(new File(fileName));
        return Result.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("download")
    public void download (String name, HttpServletResponse response) throws IOException {
        // 输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));


        // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while (( len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }

        // 关闭资源
        outputStream.close();;
        fileInputStream.close();
    }
}
