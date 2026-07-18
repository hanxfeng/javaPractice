package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 根据id删除分类，删除前需检查是否关联了菜品或套餐
     */
    @Override
    public void categoryRemove(Long id) {
    }
}
