package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.DishFlavor;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.Service.DishFlavorService;
import com.example.javaPractice.Service.DishService;
import com.example.javaPractice.dto.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save (@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        // 清理某个分类下的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);

        return Result.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 构造条件构造器
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        // 添加过滤条件
        qw.like(name != null,Dish::getName,name);
        // 添加排序条件
        qw.orderByDesc(Dish::getUpdateTime);
        // 进行分页查询
        dishService.page(pageInfo,qw);
        // 进行对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            // 根据id查询分类id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWitchFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWitchFlavor(dishDto);

        /* 清理所有菜品的缓存数据
        //Set key = redisTemplate.keys("dish_*");
        //redisTemplate.delete(key);*/

        // 清理某个分类下的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return Result.success("修改菜品成功");
    }

    /**
     * 用于对菜品进行分页查询
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        // 构建查询 key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 从 Redis 中获取缓存数据
        List<DishDto> dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 如果存在，直接返回，无需查询数据库
        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }

        // 构造查询条件
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        // 添加查询条件：status = 1
        qw.eq(Dish::getStatus,1);
        // 添加排序条件
        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(qw);

        // 添加规格数据
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = item.getName();
                dishDto.setName(categoryName);
            }

            // 获取当前菜品id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> qw2 = new LambdaQueryWrapper<>();
            qw2.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavors = dishFlavorService.list(qw2);

            dishDto.setFlavors(dishFlavors);

            return dishDto;
        }).collect(Collectors.toList());

        // 如果不存在，则查询数据库，将查询到的菜品数据存入 Redis
        redisTemplate.opsForValue().set(key, dishDtoList,1, TimeUnit.HOURS);

        return Result.success(dishDtoList);

    }

    /**
     * 用于删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids) {
        if (ids == null) {
            return Result.error("菜品ID不能为空");
        }
        boolean removed = dishService.removeById(ids);
        if (!removed) {
            return Result.error("删除失败，菜品不存在或已被删除");
        }
        return Result.success("删除菜品成功");
    }

}
