package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Entity.SetmealDish;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.Service.SetmealDishService;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.dto.SetmealDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        // 已检查，书写正确
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam int page, int pageSize, String name) {
        // 已检查，书写正确
        Page<Setmeal> newPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(name != null, Setmeal::getName, name);
        Page<Setmeal> returnPage = setmealService.page(newPage, qw);
        // 已经获得 setmeal 需要查询 setmeal 对应的菜品和套餐名称，然后将其组合为 setmealDto
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(returnPage, setmealDtoPage, "records");
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal setmeal : returnPage.getRecords()) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(SetmealDish::getSetmealId, setmeal.getId());
            List<SetmealDish> setmealDishList = setmealDishService.list(qw2);

            LambdaQueryWrapper<Category> qw3 = new LambdaQueryWrapper<>();
            qw3.eq(Category::getId, setmeal.getCategoryId());
            String categoryName = categoryService.getOne(qw3).getName();

            setmealDto.setSetmealDishes(setmealDishList);
            setmealDto.setCategoryName(categoryName);

            list.add(setmealDto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status",
            unless = "#result.code != 1")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        qw.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> setmealList = setmealService.list(qw);
        return R.success(setmealList);
    }

    /**
     * 修改套餐售卖状态
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam Long ids) {
        Setmeal setmeal = setmealService.getById(ids);
        if (setmeal == null) {
            return R.error("该套餐不存在");
        }

        setmeal.setStatus(status);
        setmealService.updateById(setmeal);
        return R.success("修改成功");
    }
}
