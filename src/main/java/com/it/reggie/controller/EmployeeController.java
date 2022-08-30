package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Employee;
import com.it.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired //通过注解的形式把接口注入进来
    private EmployeeService employeeService;


    //employee login
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //页面提交代码进行md5加密处理
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //判断有没有查询到
        if (emp==null){
            return R.error("login fail");
        }
        //比对密码
        if (!emp.getPassword().equals(password)){
            return R.error("login fail");
        }
        //查看员工状态
        if(emp.getStatus() == 0){
            return R.error("forbidden");
        }
        //登陆成功，将员工id 存入session
        request.getSession().setAttribute("empolyee",emp.getId());
        return R.success(emp);
    }
    //employee logout
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("logout success");
    }

    //新增员工
    @PostMapping
    public R<String> save( HttpServletRequest request, @RequestBody Employee employee){

        //设置初始密码123456，md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //获得当前登陆用户的ID
//        Long empID = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        employeeService.save(employee);
        return R.success("新增员工成功");


        //log.info("新增员工，员工信息: {}",employee.toString());

    }
    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize); //MybatisPlus提供的

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){//加@RequestBody 是因为传入的数据为jason格式
        log.info(employee.toString());

        Long empId = (Long)request.getSession().getAttribute("employee");//用session来获取当前用户
        employee.setUpdateUser(empId);

        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){ //路径变量
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
