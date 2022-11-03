package com.dragon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dragon.reggie.dto.DishDto;
import com.dragon.reggie.entity.Dish;
import com.dragon.reggie.entity.DishFlavor;
import com.dragon.reggie.mapper.DishMapper;
import com.dragon.reggie.service.DishFlavorService;
import com.dragon.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * TODO 新增菜品同时插入菜品对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //TODO 1. 保存菜品基本信息到dish 表
        this.save(dishDto);
        Long dishId = dishDto.getId();//对应菜品Id
        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味

        //TODO 2*.将dish 表中的Id 赋值到dish_flavor 表
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //TODO 3. 保存菜品口味基本信息到dish_flavor 表
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * TODO 根据Id 查询菜品信息和对应的 口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //TODO 1.查询菜品 基本信息 <--dish
        Dish dish = this.getById(id);

        //拷贝数据至 DishDto对象中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //TODO 2.查询菜品 口味信息 <--dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * TODO 更新菜品和口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //TODO 1.更新dish 表基本信息
        this.updateById(dishDto);

        //TODO 2.清理对应菜品口味数据 --> dish_flavor 表delete 操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //TODO 3.添加当前提交口味数据 --> dish_flavor 表insert 操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * TODO 停售菜品
     *
     */

    /**
     * TODO 删除菜品
     *
     */

}

