package com.example.javaPractice.dto;


import com.example.javaPractice.Entity.Setmeal;
import com.example.javaPractice.Entity.SetmealDish;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
