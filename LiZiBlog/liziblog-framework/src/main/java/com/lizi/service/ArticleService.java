package com.lizi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Article;

/**
 * 文章表(Article)表服务接口
 *
 * @author makejava
 * @since 2022-09-12 11:16:54
 */
public interface ArticleService extends IService<Article> {

    ResponseResult gethotArticleList(Integer pageNum, Integer pageSize);

    ResponseResult getArticleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult getNewArticle(Integer pageNum, Integer pageSize);

    ResponseResult addViewCount(Long id,String categoreName);

    ResponseResult getCreatorInfo();
}

