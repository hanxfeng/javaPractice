package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Entity.User;
import com.example.javaPractice.Service.UserService;
import com.example.javaPractice.common.BaseContext;
import com.example.javaPractice.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        // 获取手机号
        String phone = user.getPhone();

        if (!StringUtils.isEmpty(phone)) {
            // 生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            // 没有阿里云短信服务，使用log输出
            log.info("code = {}",code);

            // 保存生成的验证码到Session
            // httpSession.setAttribute(phone,code);

            // 将生成的验证码缓存到 Redis 中，设置有效期为五分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return Result.success("短信发送成功");
        }

        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登陆
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map user, HttpSession session) {
        // 获取手机号
        String phone = user.get("phone").toString();

        // 获取验证码
        String code = user.get("code").toString();

        // 从 Session 中获取保存的验证码1
        /*Object codeObj = session.getAttribute(phone);
        if (codeObj == null) {
            return Result.error("验证码已失效，请重新获取");
        }
        String codeInSession = codeObj.toString();*/

        // 从 Redis 中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 进行验证码比对
        if (code != null && codeInSession.equals(code)) {
            // 比对成功说明登陆成功
            // 判断手机号对应用户是否为新用户，如果是新用户则进行注册
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone,phone);

            User user1 = userService.getOne(qw);
            // 判断是否为新用户
            if (user1 == null) {
                // 自动进行注册
                User user2 = new User();
                user2.setPhone(phone);
                user2.setStatus(1);
                userService.save(user2);
                user1 = user2;
            }
            session.setAttribute("user",user1.getId());

            // 如果用户登录成功，就可以从 Redis 中删除验证码
            redisTemplate.delete(phone);
            return Result.success(user1);
        }
        return Result.error("登陆失败");

    }
}
