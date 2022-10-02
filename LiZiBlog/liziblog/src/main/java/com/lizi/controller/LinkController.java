package com.lizi.controller;

import com.lizi.domain.ResponseResult;
import com.lizi.service.LinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
@Api(tags = "友链接口")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @ApiModelProperty("获取所有友链")
    @GetMapping("/getAllLink")
    public ResponseResult getAllLink(Integer pageNum,Integer pageSize){
    return linkService.getAllLink( pageNum, pageSize);
    }
}
