package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址簿
     */
    @PostMapping
    // 已检查，书写正确
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();

        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 修改默认地址
     */
    @PutMapping("default")
    // 已检查，书写正确
    public R<AddressBook> setDefault(@RequestBody AddressBook a) {
        AddressBook addressBook = addressBookService.setDefault(a.getId());
        return R.success(addressBook);
     }

    /**
     * 根据id查找地址
     */
    @GetMapping("/{id}")
    // 已检查，书写正确
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        }
        else {
            return R.error("该对象不存在");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    // 已检查，书写正确
    public R<AddressBook> getDefault() {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();

        // 构建查询语句
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId);
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(qw);
        if (addressBook == null) {
            return R.error("未设置默认地址！");
        }
        return R.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    // 已检查，书写正确
    public R<List<AddressBook>> list() {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();

        // 构建查询语句
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId);
        qw.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(qw);
        if (list.isEmpty()) {
            return R.error("还未填写任何地址");
        }
        return R.success(list);
    }
}
