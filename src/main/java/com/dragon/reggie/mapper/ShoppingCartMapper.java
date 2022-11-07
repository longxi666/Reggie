package com.dragon.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dragon.reggie.controller.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
