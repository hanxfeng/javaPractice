package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Service.DishFlavorService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.dto.DishDto;
import com.example.javaPractice.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息到菜品表 dish
        this.save(dishDto);

        // 保存菜品口味数据到菜品口味表dish_flavor
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWitchFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish =this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> qw =new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(qw);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWitchFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(qw);
        // 添加(插入)当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
