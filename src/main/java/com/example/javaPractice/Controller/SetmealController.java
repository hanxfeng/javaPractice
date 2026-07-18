package com.example.javaPractice.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaPractice.Entity.R;
import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Service.SetmealService;
import com.example.javaPractice.dto.SetmealDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        return null;
    }

    /**
     * 分页查询套餐
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam int page, int pageSize, String name) {
        return null;
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        return null;
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        return null;
    }

    /**
     * 修改套餐售卖状态
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam Long ids) {
        return null;
    }
}
