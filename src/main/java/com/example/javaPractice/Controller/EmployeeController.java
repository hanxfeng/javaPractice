package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Employee;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 已检查，书写正确
    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {
        String name = employee.getName();
        String password = employee.getPassword();

        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getName, name);
        Employee sqlEmployee = employeeService.getOne(qw);

        if (sqlEmployee == null) {
            return R.error("登陆失败，该用户不存在");
        }

        if (sqlEmployee.getStatus() == 0) {
            return R.error("登陆失败，该账号已被禁用");
        }

        if (!passwordEncoder.matches(password, sqlEmployee.getPassword())){
            return R.error("登陆失败，密码错误");
        }

        session.setAttribute("userId", sqlEmployee.getId());

        return R.success(sqlEmployee);
    }

    // 已检查，书写正确
    /**
     * 员工退出登录
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.invalidate();
        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpSession session, @RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getName, employee.getName());
        if (employeeService.getOne(qw) != null) {
            return R.error("该 name 已存在");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询员工信息
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        Page<Employee> newPage = new Page<Employee>(page, pageSize);
        newPage.addOrder(OrderItem.desc("updateTime"));
        Page<Employee> pageEmployee;
        if (name != null) {
            LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
            qw.like(Employee::getName, name);
            pageEmployee = employeeService.page(newPage,qw);
        }
        else {
            pageEmployee = employeeService.page(newPage, null);
        }

        return R.success(pageEmployee);
    }

    /**
     * 更新员工信息
     */
    @PutMapping
    public R<String> update(HttpSession session, @RequestBody Employee employee) {
        if (employeeService.getById(employee.getId()) != null) {
            employeeService.updateById(employee);
        }
        else {
            return R.error("该员工不存在");
        }
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        else {
            return R.error("该员工不存在");
        }
    }
}
