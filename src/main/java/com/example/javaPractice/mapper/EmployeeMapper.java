package com.example.javaPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaPractice.Entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
