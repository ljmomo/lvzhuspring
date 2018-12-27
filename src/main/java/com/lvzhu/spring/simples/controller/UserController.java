package com.lvzhu.spring.simples.controller;

import com.lvzhu.spring.simples.service.UserService;
import com.lvzhu.spring.spring.annotation.Autowried;
import com.lvzhu.spring.spring.annotation.Controller;
import com.lvzhu.spring.spring.annotation.RequestMapping;

/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-27 0:09
 */
@Controller
@RequestMapping("/demo")
public class UserController {

    @Autowried
    private UserService userService;

    @RequestMapping("/getName")
    public String getName(String name){
        return userService.getName(name);
    }
}
