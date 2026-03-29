package com.example.javaPractice.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.Service.SetmealDishService;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.dto.SetmealDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Result.success("新增套餐成功");
    }

    public Result<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> pageDto = new Page<>();



        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<Setmeal>();
        // 根据 name 进行模糊查询
        qw.like(name != null,Setmeal::getName,name);
        // 添加排序条件
        qw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, qw);

        // 进行对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            // 获取菜品id
            Long categoryId = item.getCategoryId();
            // 查询菜品名称
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);

        return Result.success(pageDto);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);

        return Result.success("套餐数据删除成功");
    }
}
