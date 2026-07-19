package com.example.javaPractice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaPractice.Entity.AddressBook;
import com.example.javaPractice.Service.AddressBookService;
import com.example.javaPractice.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
