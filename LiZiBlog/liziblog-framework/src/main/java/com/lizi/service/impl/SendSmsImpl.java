package com.lizi.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.lizi.constants.SystemConstant;
import com.lizi.service.SendSms;
import com.lizi.utils.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "sms")
@Service
public class SendSmsImpl implements SendSms {

    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean addSendSms(String phoneNumber,Map<String ,Object> map) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5t7gnJmxYhFSjsobpzWx",
                "6xhnGlv3B4deczYei4UxoJFbjrlhMt");
        IAcsClient client = new DefaultAcsClient(profile);

        //构建请求
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        //不要改动
        request.setSysDomain("dysmsapi.aliyuncs.com");
        //不要改动
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        //自定义参数（手机号、验证码、签名、模板），左边参数为固定写法不可更改
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        //后面参数为阿里云账号中签名模板的签名名称
        request.putQueryParameter("SignName", "lizizhucloud");
        request.putQueryParameter("TemplateCode", "SMS_243940631");
        //构建短信验证码
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));
        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        System.out.println(response.getData());

        return false;
    }

}
