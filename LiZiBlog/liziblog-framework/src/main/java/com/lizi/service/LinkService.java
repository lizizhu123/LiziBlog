package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HUAWEI
* @description 针对表【sg_link(友链)】的数据库操作Service
* @createDate 2022-09-14 22:01:56
*/
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink(Integer pageNum,Integer pageSize);
}
