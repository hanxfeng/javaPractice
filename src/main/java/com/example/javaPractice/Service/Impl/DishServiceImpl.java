package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Service.DishFlavorService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.dto.DishDto;
import com.example.javaPractice.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应口味数据
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息
        save(dishDto);

        // 保存菜品对应的口味数据
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorService.save(dishFlavor);
        }
    }

    /**
     * 根据id查询菜品及其口味信息
     */
    @Override
    public DishDto getByIdWitchFlavor(Long id) {
        Dish dish = getById(id);
        if (dish == null) {
            return null;
        }

        // 查询菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(qw);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     */
    @Override
    @Transactional
    public void updateWitchFlavor(DishDto dishDto) {
        if (getById(dishDto.getId()) == null) {
            throw new CustomException("菜品不存在");
        }

        // 更新菜品基本信息
        updateById(dishDto);

        // 删除原有的口味数据，再重新插入新的口味数据
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(qw);

        for (DishFlavor dishFlavor : dishDto.getFlavors()) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorService.save(dishFlavor);
        }
    }
}
