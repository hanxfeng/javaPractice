package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.Service.setmealService;
import com.example.javaPractice.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private setmealService setmealService;

    /**
     * 根据 id 删除分类，如果关联了菜品或套餐，那么返回 “该分类关联菜品或套餐，请取消关联后再试”
     * @param id
     */
    @Override
    public void categoryRemove(Long id) {
        // 查询是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据 categoryId 进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int countDish= dishService.count(dishLambdaQueryWrapper);
        // 如果计数大于0，需要抛出业务 异常
        if (countDish > 0) {
            throw new ClassCastException("该分类关联菜品或套餐，请取消关联后再试");
        }
        // 查询是否关联了套擦
        LambdaQueryWrapper<Setmeal> setmeaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据 categoryId 进行查询
        setmeaLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int countSetema = setmealService.count(setmeaLambdaQueryWrapper);
        // 如果计数大于0,说明已经关联了套餐
        if (countSetema > 0) {
            throw new ClassCastException("该分类关联菜品或套餐，请取消关联后再试");
        }

        // 没有问题则删除分类
        super.removeById(id);
    }
}
