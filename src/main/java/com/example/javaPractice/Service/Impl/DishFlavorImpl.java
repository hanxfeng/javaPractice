package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Service.DishFlavorService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
