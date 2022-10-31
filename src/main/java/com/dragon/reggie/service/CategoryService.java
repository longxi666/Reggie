package com.dragon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.entity.Employee;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
