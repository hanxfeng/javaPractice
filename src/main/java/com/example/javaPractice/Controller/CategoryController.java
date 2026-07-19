package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Dish;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.CategoryService;
import com.example.javaPractice.mapper.CategoryMapper;
import com.example.javaPractice.mapper.DishMapper;
import com.example.javaPractice.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品或套餐分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getName, category.getName());
        if (categoryMapper.selectOne(qw) != null) {
            return R.error("该分类已存在");
        }
        categoryMapper.insert(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public R<Page<Category>> page(@RequestParam int page, int pageSize) {
        // 已检查，书写正确
        Page<Category> newPage = new Page<>(page, pageSize);
        newPage.addOrder(OrderItem.desc("sort"));

        return R.success(categoryMapper.selectPage(newPage, null));
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        LambdaQueryWrapper<Dish> qw1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> qw2 = new LambdaQueryWrapper<>();
        qw1.eq(Dish::getCategoryId, id);
        qw2.eq(Setmeal::getCategoryId, id);
        if ((dishMapper.selectCount(qw1) > 0) || (setmealMapper.selectCount(qw2) > 0)) {
            return R.error("该分类关联菜品或套餐，请取消关联后再试");
        }
        categoryMapper.deleteById(id);
        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        // 已检查，书写正确
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getId, category.getId());

        if (categoryMapper.selectOne(qw) == null) {
            return R.error("该分类不存在，无法修改");
        }
        categoryMapper.updateById(category);

        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // 已检查，书写正确
        Integer type = category.getType();

        if (type == null) {
            return R.success(categoryMapper.selectList(null));
        }

        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getType, type);
        return R.success(categoryMapper.selectList(qw));
    }
}
