package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.Orders;
import com.example.javaPractice.Service.OrderService;
import com.example.javaPractice.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    /**
     * 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
    }
}
