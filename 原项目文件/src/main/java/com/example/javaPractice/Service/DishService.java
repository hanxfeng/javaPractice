package com.example.javaPractice.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.dto.DishDto;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表，dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品的口味信息
    public DishDto getByIdWitchFlavor(Long id);

    //更新菜品信息，同时更新口味信息
    public void updateWitchFlavor(DishDto dishDto);
}
