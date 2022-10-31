package com.dragon.reggie.common;

/**
 * TODO 使用基于ThreadLocal封装工具类，用于保存和获取当期用户Id
 * 线程
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * TODO 1.创建设置值的方法
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * TODO 2.创建获取值的方法
     * @Return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
