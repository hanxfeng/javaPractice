package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Service.AddressBookService;
import com.example.javaPractice.common.CustomException;
import com.example.javaPractice.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    /**
     * 设置默认地址
     */
    @Override
    @Transactional
    public AddressBook setDefault(Long id) {
        // 获取 userId
        Long userId = BaseContext.getCurrentId();

        // 将之前的默认地址设置为 0
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId);
        qw.eq(AddressBook::getIsDefault, 1);
        AddressBook oldDefault = getOne(qw);
        if (oldDefault != null) {
            oldDefault.setIsDefault(0);
            updateById(oldDefault);
        }

        // 将指定地址设置为默认地址
        AddressBook addressBook = getById(id);
        if (addressBook == null) {
            throw new CustomException("该对象不存在");
        }
        addressBook.setIsDefault(1);
        updateById(addressBook);

        return addressBook;
    }
}
