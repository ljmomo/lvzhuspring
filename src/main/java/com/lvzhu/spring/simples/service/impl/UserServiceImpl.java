package com.lvzhu.spring.simples.service.impl;

import com.lvzhu.spring.simples.service.UserService;
import com.lvzhu.spring.spring.annotation.Service;

/**
 * @author lijun
 * @email 1796518311@qq.com
 * @since 2018-12-27 0:09
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String getName(String name) {
        System.out.println("my name is "+name);
        return name;
    }
}
