package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Entity.SetmealDish;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.dto.SetmealDto;
import com.example.javaPractice.mapper.CategoryMapper;
import com.example.javaPractice.mapper.SetmealDishMapper;
import com.example.javaPractice.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        // 已检查，书写正确
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getName, setmealDto.getName());

        if (setmealMapper.selectOne(qw) != null) {
            return R.error("该套餐已存在");
        }

        setmealMapper.insert(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        Long setmealId = setmealMapper.selectOne(qw).getId();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insert(setmealDish);
        }

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
        Page<Setmeal> returnPage = setmealMapper.selectPage(newPage, qw);
        // 已经获得 setmeal 需要查询 setmeal 对应的菜品和套餐名称，然后将其组合为 setmealDto
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(returnPage, setmealDtoPage, "records");
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal setmeal : returnPage.getRecords()) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(SetmealDish::getSetmealId, setmeal.getId());
            List<SetmealDish> setmealDishList = setmealDishMapper.selectList(qw2);

            LambdaQueryWrapper<Category> qw3 = new LambdaQueryWrapper<>();
            qw3.eq(Category::getId, setmeal.getCategoryId());
            String categoryName = categoryMapper.selectOne(qw3).getName();

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
    public R<String> delete(@RequestParam List<Long> ids) {
        return null;
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        return null;
    }

    /**
     * 修改套餐售卖状态
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam Long ids) {
        return null;
    }
}
