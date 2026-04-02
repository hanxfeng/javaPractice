package com.example.javaPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaPractice.Entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
