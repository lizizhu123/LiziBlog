package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HUAWEI
* @description 针对表【lz_message(留言表)】的数据库操作Service
* @createDate 2022-09-25 20:42:48
*/
public interface MessageService extends IService<Message> {

    ResponseResult addMessage(Message message);

    ResponseResult getMessageList(Integer pageNum, Integer pageSize);
}
