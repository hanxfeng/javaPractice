package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.dto.DishDto;
import com.example.javaPractice.mapper.DishMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * 新增菜品，同时保存对应口味数据
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
    }

    /**
     * 根据id查询菜品及其口味信息
     */
    @Override
    public DishDto getByIdWitchFlavor(Long id) {
        return null;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     */
    @Override
    @Transactional
    public void updateWitchFlavor(DishDto dishDto) {
    }
}
