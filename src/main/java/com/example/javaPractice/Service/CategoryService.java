package com.example.javaPractice.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaPractice.Entity.Category;

public interface CategoryService extends IService<Category> {
    public void categoryRemove(Long id);
}
