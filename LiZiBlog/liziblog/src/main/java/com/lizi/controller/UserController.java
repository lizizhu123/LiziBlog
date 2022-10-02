package com.lizi.controller;

import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.UserDto;
import com.lizi.domain.entity.User;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.service.SendSms;
import com.lizi.service.UserService;
import com.lizi.utils.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户表(User)表控制层
 *
 * @author makejava
 * @since 2022-09-17 13:44:35
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SendSms sendSms;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation("查询用户信息")
    @PostMapping("/getUserDetail")
    public ResponseResult getUserDetail(){
        return userService.getUserDetail();
    }

    @ApiOperation("查询用户简要信息")
    @GetMapping("/geteasyuser")
    public ResponseResult getUserEasy(Long id){
        return userService.getUserEasy(id);
    }


    @ApiOperation("更新用户信息")
    @PostMapping("/updateuser")
    public ResponseResult updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @ApiOperation("注册用户")
    @PostMapping("/register/{code}")
    public ResponseResult register(@RequestBody User user,@PathVariable("code") String code){
        System.out.println("user:"+user);
        return userService.register(user,code);
    }

    @ApiOperation("发送验证码")
    @PostMapping("/send/{phone}")
    public ResponseResult send(@PathVariable("phone") String phone){
        String code=redisCache.getCacheObject(SystemConstant.SMS_REIDS_KEY+phone);
        System.out.println(SystemConstant.SMS_REIDS_KEY+phone);
        System.out.println("sssssssssssssssssssss"+code);
        if(code!=null){
            return ResponseResult.okResult(code);
        }else{
            String code1 = String.valueOf((int)((Math.random()*9+1)*100000));
            Map<String, Object> map = new HashMap<>(1);
            map.put("code", code1);
            if( sendSms.addSendSms(phone,map)){
                redisCache.setCacheObject(SystemConstant.SMS_REIDS_KEY+phone,code1,5, TimeUnit.MINUTES);
                return ResponseResult.okResult(map);
            }else{
                return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
            }
        }
    }
}
