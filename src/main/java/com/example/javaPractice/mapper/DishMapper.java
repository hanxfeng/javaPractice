package com.example.javaPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaPractice.Entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
