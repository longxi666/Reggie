package com.dragon.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dragon.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;


@Mapper//TODO 创造Mapper 继承 MybatisPlus中的 BaseMapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
