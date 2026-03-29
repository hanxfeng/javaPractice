package com.example.javaPractice.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 新增套餐，同时保存套餐和菜品的管理
    public void saveWithDish(SetmealDto setmealDto);

    // 删除套餐,同时删除套餐和菜品的关联数据
    public void removeWithDish(List<Long> ids);
}
