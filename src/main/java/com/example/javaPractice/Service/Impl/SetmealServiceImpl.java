package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Entity.SetmealDish;
import com.example.javaPractice.Service.SetmealDishService;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.dto.SetmealDto;
import com.example.javaPractice.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 判断套餐名称是否已存在
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getName, setmealDto.getName());
        if (getOne(qw) != null) {
            throw new CustomException("该套餐已存在");
        }

        // 保存套餐基本信息
        save(setmealDto);

        // 保存套餐和菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDishService.save(setmealDish);
        }
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 检查套餐是否正在售卖，正在售卖的套餐不允许删除
        for (Long id : ids) {
            Setmeal setmeal = getById(id);
            if (setmeal != null && setmeal.getStatus() != null && setmeal.getStatus() == 1) {
                throw new CustomException("套餐" + setmeal.getName() + "正在售卖，不能删除");
            }
        }

        // 删除套餐数据
        removeByIds(ids);

        // 删除套餐和菜品的关联数据
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(qw);
    }
}
