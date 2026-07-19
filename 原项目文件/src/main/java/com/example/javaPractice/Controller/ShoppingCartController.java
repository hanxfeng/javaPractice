package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Entity.ShoppingCart;
import com.example.javaPractice.Service.ShoppingCartService;
import com.example.javaPractice.common.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将用户选择的菜品添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("出现错误，请同意浏览器使用cookie或联系管理员");
        }
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);

        // 查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            // 如果为空说明添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId,dishId);
        }
        else {
            // 不为空则说明是套餐
            qw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCart1 = shoppingCartService.getOne(qw);

        if (shoppingCart1 != null) {
            // 如果已存在数据，那么在原来的基础上加1即可
            shoppingCart.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(shoppingCart1);
        }
        else {
            // 如果不存在。新增数据
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }

        return Result.success(shoppingCart1);
    }

    /**
     * 将用户选择的菜品移除出购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        // 设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("出现错误，请同意浏览器使用cookie或联系管理员");
        }
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);

        // 查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            // 如果为空说明添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId,dishId);
        }
        else {
            // 不为空则说明是套餐
            qw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCart1 = shoppingCartService.getOne(qw);

        if (shoppingCart1.getNumber() > 1) {
            // 如果已存在数据，且数量大于1，那么在原来的基础上减1即可
            shoppingCart.setNumber(shoppingCart1.getNumber() - 1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(shoppingCart1);
        }
        else if (shoppingCart1.getNumber() == 1){
            // 如果不存在。新增数据
            shoppingCartService.removeById(shoppingCart1);
            shoppingCart1 = shoppingCart;
        }

        return Result.success(shoppingCart1);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);
        qw.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCarts= shoppingCartService.list(qw);

        return Result.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);

        shoppingCartService.remove(qw);

        return Result.success("清空购物车成功");
    }


}
