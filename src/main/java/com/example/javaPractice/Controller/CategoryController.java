package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.Category;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 用于新建菜品或套餐种类
     * @param category
     * @return
     */
    public Result<String> save(@RequestBody Category category){
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 用于对菜品/套餐分类进行分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page<Category>> page(@RequestParam int page, int pageSize){
        // 构建分页构造
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 构建 条件构造器
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // 添加排序条件
        queryWrapper.orderByDesc("sort");
        // 进行分页查询
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 用于删除菜品/套餐分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delete (Long id) {
        categoryService.categoryRemove(id);
        return Result.success("删除成功");
    }

    /**
     * 根据 id 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return  Result.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category) {
        // 创建条件构造器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<Category>();
        // 创建条件
        qw.eq(category.getType() != null,Category::getType,category.getType());
        // 添加排序条件
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(qw);
        return Result.success(list);
    }
}
