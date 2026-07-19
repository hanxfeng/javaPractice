package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.dto.DishDto;
import com.example.javaPractice.mapper.CategoryMapper;
import com.example.javaPractice.mapper.DishFlavorMapper;
import com.example.javaPractice.mapper.DishMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        // 已检查，书写正确
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishMapper.insert(dishDto);

        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorMapper.insert(dishFlavor);
        }

        // TODO：后续进行修改
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        stringRedisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // 已检查，书写正确
        Page<Dish> newPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> qw = null;
        if (name != null) {
            qw = new LambdaQueryWrapper<>();
            qw.like(Dish::getName, name);
        }

        Page<Dish> returnPage = dishMapper.selectPage(newPage, qw);

        BeanUtils.copyProperties(returnPage, dishDtoPage, "records");

        List<Dish> records = returnPage.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).toList();

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        // 已检查，书写正确
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            return R.error("该菜品不存在");
        }

        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorMapper.selectList(qw);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(list);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        // 已检查，书写正确
        if (dishMapper.selectById(dishDto.getId()) == null) {
            return R.error("菜品不存在");
        }
        dishMapper.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorMapper.delete(qw);
        for (DishFlavor dishFlavor : dishDto.getFlavors()) {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavorMapper.insert(dishFlavor);
        }
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询菜品列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) throws JsonProcessingException {
        // 已检查，书写正确
        if (dish.getName() == null && dish.getCategoryId() == null) {
            return R.error("传入参数为空");
        }
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        String redisData = stringRedisTemplate.opsForValue().get(key);
        List<DishDto> dishDtos;
        if (redisData == null) {
            dishDtos = new ArrayList<>();
            LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
            qw.eq(dish.getName() != null, Dish::getName, dish.getName());
            qw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
            List<Dish> dishTemp = dishMapper.selectList(qw);

            if (dishTemp.isEmpty()) {
                return R.error("菜品不存在");
            }

            for (Dish dish1 : dishTemp) {
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(dish1, dishDto);

                LambdaQueryWrapper<DishFlavor> qw2 = new LambdaQueryWrapper<>();
                qw2.eq(DishFlavor::getDishId, dish1.getId());
                List<DishFlavor> list = dishFlavorMapper.selectList(qw2);

                dishDto.setFlavors(list);
                dishDtos.add(dishDto);
            }
            String writeRedis = objectMapper.writeValueAsString(dishDtos);
            stringRedisTemplate.opsForValue().set(key, writeRedis);
            return R.success(dishDtos);
        }
        else {
            List<DishDto> dishDtos2 = objectMapper.readValue(redisData, new TypeReference<List<DishDto>>() {});
            return R.success(dishDtos2);
        }
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        // 已检查，书写正确
        if (ids == null) {
            return R.error("错误！id 为空");
        }

        if (dishMapper.selectById(ids) == null) {
            return R.error("菜品不存在");
        }

        dishMapper.deleteById(ids);
        return R.success("删除菜品成功");
    }
}
