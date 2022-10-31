package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.reggie.common.R;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TODO 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * TODO 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * TODO 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //TODO 1.创造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //TODO 2.条件构造器对象
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //TODO 3.添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //TODO 4.进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * TODO 根据ID 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){ //参数修改为 --> ids
        log.info("删除分类，id为{}",ids);
        //TODO 1.自制并调用自制删除方法 （ remove（Long id））    -->CategoryService
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * TODO 根据Id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}... ",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功!");
    }

}
