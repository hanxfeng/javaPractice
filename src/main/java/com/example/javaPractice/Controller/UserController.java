package com.example.javaPractice.Controller;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.User;
import com.example.javaPractice.Service.UserService;
import com.example.javaPractice.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送手机验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(0, 10000));
        stringRedisTemplate.opsForValue().set(user.getPhone(), code, 5, TimeUnit.MINUTES);
        log.info("验证码为：" + code);
        return R.success("短信发送成功");
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> user, HttpSession session) {
        String phone = user.get("phone");
        String code = user.get("code");

        String redisCode = stringRedisTemplate.opsForValue().get(phone);
        if (redisCode != null && Objects.equals(code, redisCode)) {
            return R.error("验证码或手机号错误");
        }

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPhone, phone);
        User loginUser = userMapper.selectOne(qw);
        if (loginUser == null) {
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setStatus(1);
            userMapper.insert(newUser);
        }
        loginUser = userMapper.selectOne(qw);
        session.setAttribute("user", loginUser.getId());
        stringRedisTemplate.delete(phone);
        return R.success(loginUser);
    }
}
