package com.example.javaPractice.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaPractice.Entity.Orders;

public interface OrderService extends IService<Orders> {

    // 用户下单
    public void submit(Orders orders);
}
