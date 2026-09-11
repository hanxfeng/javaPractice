package com.example.javaPractice.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaPractice.Entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

    /**
     * 设置默认地址
     */
    AddressBook setDefault(Long id);
}
