package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.OrderDetail;
import com.example.javaPractice.Entity.Orders;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.ShoppingCart;
import com.example.javaPractice.Service.OrderService;
import com.example.javaPractice.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        // 获取用户 id
        Long id = BaseContext.getCurrentId();

        // 获取用户购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, id);
        ShoppingCart shoppingCart = shoppingCartMapper.selectOne(qw);

        // 如果购物车为空则返回报错
        if (shoppingCart == null) {
            return R.error("购物车为空，无法下单");
        }

        // 生成订单号
        Long orderId = IdWorker.getId();

        // 将购物车数据转为订单明细数据
        OrderDetail orderDetail = new OrderDetail();


        return null;
    }
}
