package com.example.javaPractice.Service.Impl;

import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.setmealService;
import com.example.javaPractice.mapper.SetmealMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class setmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements setmealService {
}
