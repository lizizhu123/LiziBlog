package com.lizi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lizi.domain.entity.Article;
import com.lizi.domain.vo.HotArticleVo;

import java.util.List;

/**
 * 文章表(Article)表数据库访问层
 *
 * @author makejava
 * @since 2022-09-12 11:17:06
 */
public interface ArticleMapper extends BaseMapper<Article> {
    List<HotArticleVo> hotArticle();
    Long viewCount(Long id);
}

