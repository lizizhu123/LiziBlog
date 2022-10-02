package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HUAWEI
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2022-09-17 16:52:33
*/
public interface UserService extends IService<User> {
    ResponseResult getUserDetail();

    ResponseResult getUserEasy(Long id);

    ResponseResult updateUser(User user);

    ResponseResult register(User user,String code);
}
