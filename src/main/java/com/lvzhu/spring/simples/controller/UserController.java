package com.lvzhu.spring.simples.controller;

import com.lvzhu.spring.simples.service.UserService;
import com.lvzhu.spring.spring.annotation.Autowried;
import com.lvzhu.spring.spring.annotation.Controller;
import com.lvzhu.spring.spring.annotation.RequestMapping;
import com.lvzhu.spring.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-27 0:09
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowried
    private UserService userService;

    @RequestMapping("/getName")
    public String getName(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name){
        return userService.getName(name);
    }

    @RequestMapping("/getHello")
    public String getHello(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name){
        return userService.getName(name);
    }
}
