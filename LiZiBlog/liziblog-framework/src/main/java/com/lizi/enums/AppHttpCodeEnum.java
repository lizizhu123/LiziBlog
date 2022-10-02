package com.lizi.enums;

/**
 * 定义响应结果信息
* */
public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
    PHONENUMBER_EXIST(502,"手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    PAGE_TOTAL_NULL(506,"数据为空"),
    FILE_TYPE_ERROR(507,"文件类型错误"),
    REQUIRE_PASSWORD(508, "必需填写密码"),
    REQUIRE_NICKNAME(509, "必需填写昵称"),
    REQUIRE_PHONE(510, "必需填写手机号"),
    ERROR_SMS_CODE(511, "验证码错误");


    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}