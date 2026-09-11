package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品或套餐分类
     */
    @PostMapping
    @CacheEvict(value = "categoryCache", allEntries = true)
    public R<String> save(@RequestBody Category category) {
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getName, category.getName());
        if (categoryService.getOne(qw) != null) {
            return R.error("该分类已存在");
        }
        categoryService.save(category);
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

        return R.success(categoryService.page(newPage, null));
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    @CacheEvict(value = "categoryCache", allEntries = true)
    public R<String> delete(Long id) {
        categoryService.categoryRemove(id);
        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     */
    @PutMapping
    @CacheEvict(value = "categoryCache", allEntries = true)
    public R<String> update(@RequestBody Category category) {
        // 已检查，书写正确
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getId, category.getId());

        if (categoryService.getOne(qw) == null) {
            return R.error("该分类不存在，无法修改");
        }
        categoryService.updateById(category);

        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    @Cacheable(value = "categoryCache", key = "#category.type", unless = "#result.code != 1")
    public R<List<Category>> list(Category category) {
        // 已检查，书写正确
        Integer type = category.getType();

        if (type == null) {
            return R.success(categoryService.list(null));
        }

        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(Category::getType, type);
        return R.success(categoryService.list(qw));
    }
}
