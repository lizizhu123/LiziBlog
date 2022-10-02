package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.LoginUser;
import com.lizi.domain.entity.User;
import com.lizi.domain.vo.LoginUserVo;
import com.lizi.domain.vo.UserEasyVo;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.service.UserService;
import com.lizi.mapper.UserMapper;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.RedisCache;
import com.lizi.utils.SecurityUtils;
import org.apache.catalina.security.SecurityUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
* @author HUAWEI
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2022-09-17 16:52:33
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PasswordEncoder PasswordEncoder;

    @Override
    public ResponseResult getUserDetail() {
        Long userId= null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        if(Objects.isNull(userId)){
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        LoginUser loginUser = null;
        loginUser = redisCache.getCacheObject(SystemConstant.BLOG_LOGIN_REDIS_KEY + String.valueOf(userId));

        
        LoginUserVo loginUserVo = BeanCopyUtil.copyBean(loginUser.getUser(), LoginUserVo.class);

        return ResponseResult.okResult(loginUserVo);
    }

    @Override
    public ResponseResult getUserEasy(Long id) {
        if(id>0){
            UserEasyVo userEasyVo = BeanCopyUtil.copyBean(getById(id), UserEasyVo.class);
            return ResponseResult.okResult(userEasyVo);
        }
        else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"无该用户");
        }
    }

    @Override
    public ResponseResult updateUser(User user) {
        Long id=SecurityUtils.getUserId();
        //只更新指定字段
        LambdaUpdateWrapper<User> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getId,id);
        lambdaUpdateWrapper.set(User::getNickName,user.getNickName());
        lambdaUpdateWrapper.set(User::getEmail,user.getEmail());
        lambdaUpdateWrapper.set(User::getProduction,user.getProduction());
        lambdaUpdateWrapper.set(User::getSex,user.getSex());
        boolean flag=update(lambdaUpdateWrapper);
        LoginUser loginUser=new LoginUser();
        loginUser.setUser(getById(id));
        redisCache.setCacheObject(SystemConstant.BLOG_LOGIN_REDIS_KEY+
                String.valueOf(id),loginUser);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"更新失败");
        }else{
            return ResponseResult.okResult();
        }

    }

    @Override
    public ResponseResult register(User user,String code) {
        String code1=redisCache.getCacheObject(SystemConstant.SMS_REIDS_KEY+user.getPhonenumber());
        System.out.println(code+"----------"+code1);
        if(code==""||code1==""||!code.equals(code1)){
            return ResponseResult.errorResult(AppHttpCodeEnum.ERROR_SMS_CODE);
        }
        //对数据进行校验
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_PASSWORD);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_NICKNAME);
        }
        if(!StringUtils.hasText(user.getPhonenumber())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_PHONE);
        }
        //对数据进行判断是否存在
        if(UserNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(PhoneExist(user.getPhonenumber())){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if(EmailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        //对密码加密
        String password = PasswordEncoder.encode(user.getPassword());
        user.setPassword(password);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }


    private boolean UserNameExist(String userName) {
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName,userName);
        return count(lambdaQueryWrapper)>0;
    }
    private boolean PhoneExist(String phone) {
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhonenumber,phone);
        return count(lambdaQueryWrapper)>0;
    }
    private boolean EmailExist(String email) {
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        return count(lambdaQueryWrapper)>0;
    }
}




