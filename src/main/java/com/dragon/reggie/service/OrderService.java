package com.dragon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dragon.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {

    /**
     * TODO 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
