package com.lizi.controller;


import com.lizi.domain.ResponseResult;
import com.lizi.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags="上传文件")
@RestController
public class UpLoadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation("上传头像")
    @PostMapping("/upload")
    public ResponseResult uploadImg(MultipartFile img){
        System.out.println("===============>"+img);
        return uploadService.uploadImg(img);
    }
}
