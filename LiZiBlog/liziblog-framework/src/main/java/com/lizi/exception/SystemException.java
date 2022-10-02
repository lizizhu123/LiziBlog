package com.lizi.exception;

import com.lizi.enums.AppHttpCodeEnum;

/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 * 统一异常处理
 */
public class SystemException extends RuntimeException{

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

}
