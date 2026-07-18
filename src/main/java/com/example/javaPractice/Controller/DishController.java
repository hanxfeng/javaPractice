package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.dto.DishDto;
import com.example.javaPractice.mapper.DishFlavorMapper;
import com.example.javaPractice.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishMapper.insert(dishDto);
        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorMapper.insert(dishFlavor);
        }
        // TODO：后续进行修改
        String key = "DISH";
        stringRedisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // TODO:没做完 ；

        Page<Dish> newPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> qw;
        if (name != null) {
            qw = new LambdaQueryWrapper<>();
            qw.like(Dish::getName, name);
        }
        else{
            qw = null;
        }
        Page<Dish> returnPage = dishMapper.selectPage(newPage, qw);

        return null;
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        return null;
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        return null;
    }

    /**
     * 根据条件查询菜品列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        return null;
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        return null;
    }
}
