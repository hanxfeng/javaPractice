package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Service.AddressBookService;
import com.example.javaPractice.mapper.AddressBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址簿
     */
    @PostMapping
    // 已检查，书写正确
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();

        addressBook.setUserId(userId);
        addressBookMapper.insert(addressBook);
        return R.success(addressBook);
    }

    /**
     * 修改默认地址
     */
    @PutMapping("default")
    // 已检查，书写正确
    public R<AddressBook> setDefault(@RequestBody AddressBook a) {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();
        Long id = a.getId();

        // 将之前的默认地址设置为 0
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId);
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook1 = addressBookMapper.selectOne(qw);
        if (addressBook1 != null) {
            addressBook1.setIsDefault(0);
            addressBookMapper.updateById(addressBook1);
        }

        AddressBook addressBook = addressBookMapper.selectById(id);
        addressBook.setIsDefault(1);
        addressBookMapper.updateById(addressBook);
        return R.success(addressBook);
     }

    /**
     * 根据id查找地址
     */
    @GetMapping("/{id}")
    // 已检查，书写正确
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookMapper.selectById(id);
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
        AddressBook addressBook = addressBookMapper.selectOne(qw);
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
        List<AddressBook> list = addressBookMapper.selectList(qw);
        if (list.isEmpty()) {
            return R.error("还未填写任何地址");
        }
        return R.success(list);
    }
}
