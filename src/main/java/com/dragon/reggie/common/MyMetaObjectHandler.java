package com.dragon.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * TODO 自定义元数据对象处理器
 */

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * TODO 1.设置插入时自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());// 如何动态地获得当前用户？ --> common.BaseContext
        metaObject.setValue("updateUser",BaseContext.getCurrentId());


    }
    /**
     * TODO 2.设置更新时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
