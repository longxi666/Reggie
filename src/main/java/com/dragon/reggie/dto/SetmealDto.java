package com.dragon.reggie.dto;

import com.dragon.reggie.entity.Setmeal;
import com.dragon.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
