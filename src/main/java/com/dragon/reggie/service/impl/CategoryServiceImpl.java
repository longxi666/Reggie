package com.dragon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dragon.reggie.common.CustomException;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.entity.Dish;
import com.dragon.reggie.entity.Setmeal;
import com.dragon.reggie.mapper.CategoryMapper;
import com.dragon.reggie.service.CategoryService;
import com.dragon.reggie.service.DishService;
import com.dragon.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * TODO 根据Id删除分类，但删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {

        //TODO 1.1 查询当前分类是否关联了菜品
        //关联则抛异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //添加查询条件， 根据id
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            throw new CustomException("当前分类关联了菜品， 不能删");
        }

        //TODO 1.2 查询当前分类是否关联了套餐
        //关联则抛异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //添加查询条件， 根据id
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类关联了套餐， 不能删");
        }
        //TODO 2.执行删除操作
        super.removeById(id);

    }
}
