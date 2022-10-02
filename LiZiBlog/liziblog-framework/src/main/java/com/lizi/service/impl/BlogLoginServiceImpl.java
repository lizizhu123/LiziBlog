package com.lizi.service.impl;

import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.UserDto;
import com.lizi.domain.entity.LoginUser;
import com.lizi.domain.entity.User;
import com.lizi.domain.vo.LoginUserVo;
import com.lizi.service.BlogLoginService;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.JwtUtil;
import com.lizi.utils.RedisCache;
import com.lizi.utils.SecurityUtils;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    //SpringSecurity中默认不在IOC容器中，需要手动配置blog.com.lizi.config.SecurityConfig.java
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    /**
     * 登陆
     * */
    @Override
    public ResponseResult login(UserDto user) {
        //使用SpringSecurity进行登陆认证操作
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断认证是否通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser=(LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        String jwt = JwtUtil.createJWT(String.valueOf(userId));
        //把用户信息存入Redis
        redisCache.setCacheObject(SystemConstant.BLOG_LOGIN_REDIS_KEY+userId,loginUser);
        //封装token和userinfo返回前端
        LoginUserVo loginUserVo = BeanCopyUtil.copyBean(loginUser.getUser(), LoginUserVo.class);
        Map dataMap=new HashMap();
        dataMap.put("token",jwt);
        dataMap.put("userInfo",loginUserVo);
        return ResponseResult.okResult(dataMap);
    }

    /**
     * 登出
     * 删除redis中的用户信息
     * */
    @Override
    public ResponseResult logout() {
        //获取Redis的key：userID
        //userId从token中获取
        //从SecurityContextHolder中获取loginuser  只存在于一个线程
        /**
         *  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         *  LoginUser loginUser= (LoginUser) authentication.getPrincipal();
         *  //获取Userid
         *  Long userid=loginUser.getUser().getId();
         *  经常需要使用到userId
         *  该段代码封装为一个工具类使用SecurityUtils
         */
        Long userId = SecurityUtils.getUserId();//等于上面3行代码
        redisCache.deleteObject(SystemConstant.BLOG_LOGIN_REDIS_KEY+String.valueOf(userId));
        return ResponseResult.okResult();
    }
}
