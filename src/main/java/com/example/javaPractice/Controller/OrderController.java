package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.*;
import com.example.javaPractice.Service.OrderService;
import com.example.javaPractice.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    // 已检查，书写正确
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
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(qw);

        // 如果购物车为空则返回报错
        if (shoppingCarts.isEmpty()) {
            return R.error("购物车为空，无法下单");
        }

        // 生成订单号
        Long orderId = IdWorker.getId();
        orders.setNumber(orderId);

        // 将购物车数据转为订单明细数据
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setImage(item.getImage());

            return orderDetail;
        }).toList();

        // 计算实收金额和保存订单数据
        BigDecimal amount = BigDecimal.valueOf(0);
        for (OrderDetail orderDetail : orderDetails) {
            amount = amount.add(orderDetail.getAmount().multiply(BigDecimal.valueOf(orderDetail.getNumber())));
            orderDetailMapper.insert(orderDetail);

        }

        // 设置订单数据
        orders.setStatus(1);
        orders.setUserId(shoppingCarts.get(0).getUserId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setAmount(amount);
        User user = userMapper.selectById(shoppingCarts.get(0).getUserId());
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId());
        if (addressBook == null) {
            return R.error("请先填写地址信息");
        }
        orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()
        +addressBook.getDetail());
        orderMapper.insert(orders);

        // 清空购物车数据
        for (ShoppingCart shoppingCart : shoppingCarts) {
         shoppingCartMapper.deleteById(shoppingCart.getId());
        }

        return R.success("下单成功");
    }
}
