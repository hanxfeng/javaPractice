package com.example.javaPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaPractice.Entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
