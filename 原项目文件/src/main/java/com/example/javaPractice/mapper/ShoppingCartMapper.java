package com.example.javaPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaPractice.Entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
