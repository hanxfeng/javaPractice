package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.ShoppingCart;
import com.example.javaPractice.Service.ShoppingCartService;
import com.example.javaPractice.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    // 已检查，书写正确
    /**
     * 添加菜品/套餐到购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 获取用户 id
        Long userID = BaseContext.getCurrentId();
        shoppingCart.setUserId(userID);

        // 查询当前用户购物车中是否已有相同商品
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userID);
        if (shoppingCart.getDishId() == null) {
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else if (shoppingCart.getSetmealId() == null) {
            qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        else {
            qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart existing = shoppingCartMapper.selectOne(qw);
        if (existing != null) {
            // 已存在，数量 +1
            existing.setNumber(existing.getNumber() + 1);
            shoppingCartMapper.updateById(existing);
            return R.success(existing);
        }

        // 不存在，新增记录
        shoppingCart.setNumber(1);
        shoppingCartMapper.insert(shoppingCart);
        return R.success(shoppingCart);
    }

    // 已检查，书写正确
    /**
     * 从购物车中减少菜品/套餐数量
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        // 获取用户 id
        Long userID = BaseContext.getCurrentId();

        // 获取减少的菜品/套餐
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userID);
        if (shoppingCart.getDishId() == null) {
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        else if (shoppingCart.getSetmealId() == null) {
            qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        else {
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        ShoppingCart sc = shoppingCartMapper.selectOne(qw);

        // 判断是否存在计数异常
        if (sc.getNumber() <= 0) {
            return R.error("未知错误");
        }

        // 根据数量不同返回不同结果
        if (sc.getNumber() == 1) {
            shoppingCartMapper.deleteById(sc);
            sc.setNumber(sc.getNumber()-1);
            return R.success(sc);
        }
        else{
            sc.setNumber(sc.getNumber()-1);
            shoppingCartMapper.updateById(sc);
            return R.success(sc);
        }
    }

    // 已检查，书写正确
    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        // 获取用户 id
        Long userId = BaseContext.getCurrentId();

        // 根据用户 id 查询购物车
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        qw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(qw);

        return R.success(shoppingCarts);
    }

    // 已检查，书写正确
    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        // 获取用户 id
        Long userId = BaseContext.getCurrentId();

        // 根据用户 id 删除购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        shoppingCartMapper.delete(qw);

        return R.success("清空购物车成功");
    }
}
