package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Service.AddressBookService;
import com.example.javaPractice.common.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址薄
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId() );
        addressBookService.save(addressBook);
        return  Result.success(addressBook);
    }

    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        // 先将所有的地址信息中用来表示是否是默认地址的 is_default 改为0
        LambdaUpdateWrapper<AddressBook> qw = new LambdaUpdateWrapper<>();
        qw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        qw.set(AddressBook::getIsDefault,0);

        addressBookService.update(qw);

        // 再将新增的默认地址的 is_default 改为1
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return Result.success(addressBook);
    }

    /**
     * 根据id查找地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        }
        else {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getId,BaseContext.getCurrentId());
        qw.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(qw);

        if (addressBook != null) {
            return Result.error("没有找到该对象");
        }
        else {
            return Result.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        qw.orderByDesc(AddressBook::getUpdateTime);

        return Result.success(addressBookService.list(qw));
    }


}
