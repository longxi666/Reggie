package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.reggie.common.R;
import com.dragon.reggie.entity.Category;
import com.dragon.reggie.entity.Employee;
import com.dragon.reggie.service.CategoryService;
import com.dragon.reggie.service.EmployeeService;
import com.dragon.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * TODO 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * TODO 处理登录逻辑
     * @param request,employee
     * @return R<Employee>
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee){

        //TODO 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //TODO 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);// 用户名唯一

        //TODO 3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败,用户不存在");
        }

        //TODO 4、密码比对，如果不一致则返回登录失败结果
        if( ! emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //TODO 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //TODO 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * TODO 处理退出逻辑
     * @param request
     * @return
     */
    @PostMapping ("/logout")
    public R<String> logout(HttpServletRequest request){
        //TODO 1.清理Session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return  R.success("退出成功！");
    }

    /**
     * TODO 添加员工
     * @param employee
     * @return
     */
    @PostMapping//无需书写路径
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //TODO 1.设置表单中未填写的员工信息
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));//设置初始密码,需要加密

        //使用
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //Long empID = (long) request.getSession().getAttribute("employee");//获取当前用户的ID

        //employee.setCreateUser(empID);
        //employee.setUpdateUser(empID);

        employeeService.save(employee);

        return R.success("新增员工成功");

    }

    /**
     * TODO 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //TODO 1.构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //TODO 2.构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //TODO 3.添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //TODO 4.添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //TODO 5 .执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * TODO 修改员工信息和状态
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //Q: 修改时传入ID精度丢失 --> 导入对象映射器 JacksonObjectMapper
        log.info(employee.toString());
        //Long empId = (Long)request.getSession().getAttribute("employee");

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * TODO 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息 ...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        else{
            return R.error("未查询到员工信息");
        }
    }
}
