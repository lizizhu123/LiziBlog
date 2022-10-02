package com.lizi.controller;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Message;
import com.lizi.service.MessageService;
import com.lizi.utils.Limit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Api(tags="留言")
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @Limit(key = "addMessage", permitsPerSecond = 1, timeout = 500, timeunit = TimeUnit.MILLISECONDS,msg = "当前排队人数较多，请稍后再试！")
    @ApiOperation("发表留言")
    @PostMapping("/addmessage")
    public ResponseResult addMessafe(@RequestBody Message message){
        return messageService.addMessage(message);
    }
    @ApiOperation("获取留言列表")
    @GetMapping("/getmessagelist")
    public ResponseResult getMessageList(Integer pageNum,Integer pageSize){
        return messageService.getMessageList(pageNum,pageSize);
    }
}
