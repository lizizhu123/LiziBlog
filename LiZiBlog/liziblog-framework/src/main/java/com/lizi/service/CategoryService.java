package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HUAWEI
* @description 针对表【sg_category(分类表)】的数据库操作Service
* @createDate 2022-09-12 22:05:11
*/
public interface CategoryService extends IService<Category> {

    ResponseResult getCategory();
}
