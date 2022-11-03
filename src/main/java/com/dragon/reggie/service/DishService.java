package com.dragon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dragon.reggie.dto.DishDto;
import com.dragon.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //TODO 新增菜品同时插入菜品对应的口味数据  ——> dish 、 dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //TODO 根据Id 查询Dish 和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //TODO 更新菜品和口味信息
    public void updateWithFlavor(DishDto dishDto);
}
