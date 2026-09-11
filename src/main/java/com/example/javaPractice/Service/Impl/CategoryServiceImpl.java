package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除前需检查是否关联了菜品或套餐
     */
    @Override
    public void categoryRemove(Long id) {
        LambdaQueryWrapper<Dish> qw1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> qw2 = new LambdaQueryWrapper<>();
        qw1.eq(Dish::getCategoryId, id);
        qw2.eq(Setmeal::getCategoryId, id);
        if ((dishService.count(qw1) > 0) || (setmealService.count(qw2) > 0)) {
            throw new CustomException("该分类关联菜品或套餐，请取消关联后再试");
        }
        removeById(id);
    }
}
