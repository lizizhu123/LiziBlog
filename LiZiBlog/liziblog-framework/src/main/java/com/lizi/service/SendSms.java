package com.lizi.service;

import java.util.Map;

//接口 传入参数为电话号码，模板编号，验证码
public interface SendSms {

    boolean addSendSms(String PhoneNumbers ,Map<String ,Object> map);

}
