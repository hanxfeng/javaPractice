package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.mapper.DishMapper;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
