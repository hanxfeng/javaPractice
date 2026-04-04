package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Employee;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.mapper.EmployeeMapper;
import com.example.javaPractice.Service.EmployeeService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private EmployeeService employeeService;

    /**
     * 用于进行登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login (HttpServletRequest request, @RequestBody Employee employee) {
        // 对密码进行 md5 加密处理（数据库中的密码是被 md5 加密过的）
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据用户名查询用户数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没有查询到则返回登陆失败结果
        if (emp == null) {
            return Result.error("登陆失败，该用户不存在");
        }

        // 比对密码是否相同
        if (!password.equals(emp.getPassword())) {
            return Result.error("登陆失败，密码错误");
        }

        // 确认员工状态是否是已禁用
        if (emp.getStatus() == 0) {
            return Result.error("登陆失败，该账号已被禁用");
        }

        // 登陆成功
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 用于退出登陆
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 清理 Session 中保存的当前登陆员工的 id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 用于添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}",employee);
        // 设置一个统一的初始密码，进行 md5 加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // 获取当前用户 id
        // Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);

        employeeService.save(employee);

        return Result.success("新增员工成功");
    }

    /**
     * 查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 构建分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 构建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 进行查询
        employeeMapper.selectPage(pageInfo,queryWrapper);

        return Result.success(pageInfo);
    }


    /**
     * 用于更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        // employee.setUpdateTime(LocalDateTime.now());
        // mployee.setCreateUser((Long)request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    /**
     * 根据 id 查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return Result.error(("该员工不存在"));
        }
        return Result.success(employee);
    }
}
