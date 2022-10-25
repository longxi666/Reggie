package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dragon.reggie.common.R;
import com.dragon.reggie.entity.Employee;
import com.dragon.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

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
}
