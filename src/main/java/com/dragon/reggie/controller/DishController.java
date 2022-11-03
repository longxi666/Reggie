package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.reggie.common.R;
import com.dragon.reggie.dto.DishDto;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.entity.Dish;
import com.dragon.reggie.service.CategoryService;
import com.dragon.reggie.service.DishFlavorService;
import com.dragon.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * TODO 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        //TODO 1.自制DTO 类封装两数据类型 --> (DishDto)
        //TODO 2.自制并使用方法saveWithFlavor --> DishService(Impl)

        dishService.saveWithFlavor(dishDto);

        return R.success("添加菜品成功！");
    }

    /**
     * TODO 菜品信息分类查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        //TODO 1.构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //TODO 2.条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //TODO 3.添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);

        //TODO 4.添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //TODO 5.执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //TODO 6.对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list =records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类Id
            //根据Id 查找分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);

    }

    /**
     * TODO 根据Id 查询菜品信息和对应的 口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * TODO 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功！");
    }

}
