package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Entity.OrderDetail;
import com.example.javaPractice.Entity.Orders;
import com.example.javaPractice.Entity.ShoppingCart;
import com.example.javaPractice.Entity.User;
import com.example.javaPractice.Service.AddressBookService;
import com.example.javaPractice.Service.OrderDetailService;
import com.example.javaPractice.Service.OrderService;
import com.example.javaPractice.Service.ShoppingCartService;
import com.example.javaPractice.Service.UserService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取用户 id
        Long id = BaseContext.getCurrentId();

        // 获取用户购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, id);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);

        // 如果购物车为空则返回报错
        if (shoppingCarts.isEmpty()) {
            throw new CustomException("购物车为空，无法下单");
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

        // 计算实收金额和保存订单明细数据
        BigDecimal amount = BigDecimal.valueOf(0);
        for (OrderDetail orderDetail : orderDetails) {
            amount = amount.add(orderDetail.getAmount().multiply(BigDecimal.valueOf(orderDetail.getNumber())));
            orderDetailService.save(orderDetail);
        }

        // 设置订单数据
        orders.setStatus(1);
        orders.setUserId(shoppingCarts.get(0).getUserId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setAmount(amount);
        User user = userService.getById(shoppingCarts.get(0).getUserId());
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("请先填写地址信息");
        }
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName()
                + addressBook.getDistrictName() + addressBook.getDetail());
        save(orders);

        // 清空购物车数据
        LambdaQueryWrapper<ShoppingCart> qw2 = new LambdaQueryWrapper<>();
        qw2.eq(ShoppingCart::getUserId, id);
        shoppingCartService.remove(qw2);
    }
}
