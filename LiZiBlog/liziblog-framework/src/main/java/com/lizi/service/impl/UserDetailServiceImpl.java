package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lizi.domain.entity.LoginUser;
import com.lizi.domain.entity.User;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

//默认UserDetailService会在内存中查找  需要改为用mapper在数据库中查找
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<User>();
        queryWrapper.eq(User::getUserName,username);
        User user=userMapper.selectOne(queryWrapper);
        //判断是否查到用户  如果没查到抛出异常
        if(Objects.isNull(user)){
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        //查询到用户返回用户信息

        //TODO 查询权限信息封装

        return new LoginUser(user);
    }
}
