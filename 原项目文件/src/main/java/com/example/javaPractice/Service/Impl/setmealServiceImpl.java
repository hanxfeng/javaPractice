package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Entity.SetmealDish;
import com.example.javaPractice.Service.SetmealDishService;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.dto.SetmealDto;
import com.example.javaPractice.mapper.SetmealMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class setmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
     public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息
        this.save(setmealDto);
        // 保存套餐与菜品的关联关系
        List<SetmealDish> setmealDishs = setmealDto.getSetmealDishes();
        setmealDishs.stream().map((item)-> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishs);
    }

    /**
     * 删除套餐，同时删除套餐关联的数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId,ids);
        qw.eq(Setmeal::getStatus,1);

        int count = this.count(qw);

        // 如果不能删除（正在售卖），抛出一个业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        // 然后再删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
        qw2.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(qw2);
    }
}
