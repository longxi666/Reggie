package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.reggie.common.R;
import com.dragon.reggie.dto.DishDto;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.entity.Dish;
import com.dragon.reggie.entity.DishFlavor;
import com.dragon.reggie.service.CategoryService;
import com.dragon.reggie.service.DishFlavorService;
import com.dragon.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate redisTemplate;

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

        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");  -- 所有
        //redisTemplate.delete(keys);

        ////清理某个菜品的缓存数据
        String keys ="dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

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

        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");  -- 所有
        //redisTemplate.delete(keys);

        ////清理某个菜品的缓存数据
        String keys ="dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("修改菜品成功！");
    }
/*

    */
/**
     * TODO 根据条件查询对应的菜品数据
     * @param dish
     * @return
     *//*

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1); //   仅查询状态为在售的菜品
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        return R.success(dishList);

    }
*/
@GetMapping("/list")
public R<List<DishDto>> list(Dish dish){
    List<DishDto> dishDtoList = null;

    //动态构造Key
    String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

    //从Redis 中获取缓存数据
    dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);

    if (dishDtoList != null) {
        //存在则返回，无需查询数据库
        R.success(dishDtoList);

    }

    //构造查询条件
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
    //添加条件，查询状态为1（起售状态）的菜品
    queryWrapper.eq(Dish::getStatus,1);

    //添加排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

    List<Dish> list = dishService.list(queryWrapper);

    dishDtoList = list.stream().map((item) -> {
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(item,dishDto);

        Long categoryId = item.getCategoryId();//分类id
        //根据id查询分类对象
        Category category = categoryService.getById(categoryId);

        if(category != null){
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }

        //当前菜品的id
        Long dishId = item.getId();
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
        //SQL:select * from dish_flavor where dish_id = ?
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }).collect(Collectors.toList());

    //不存在则查询数据库，及那个查询到的数据缓存到Redis 中
    redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

    return R.success(dishDtoList);
}
}
