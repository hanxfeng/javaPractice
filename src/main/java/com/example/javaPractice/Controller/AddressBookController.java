package com.example.javaPractice.Controller;

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
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        return null;
    }

    /**
     * 修改默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        return null;
    }

    /**
     * 根据id查找地址
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        return null;
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        return null;
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        return null;
    }
}
