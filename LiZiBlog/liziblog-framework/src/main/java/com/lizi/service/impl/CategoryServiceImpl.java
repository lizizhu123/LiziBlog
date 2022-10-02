package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Article;
import com.lizi.domain.entity.Category;
import com.lizi.domain.vo.CategoryVo;
import com.lizi.service.ArticleService;
import com.lizi.service.CategoryService;
import com.lizi.mapper.CategoryMapper;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author HUAWEI
* @description 针对表【sg_category(分类表)】的数据库操作Service实现
 * 需求：
 * 1、只查询有发布正式文章的分类
 * 2、必须是正常状态的分类
* @createDate 2022-09-12 22:05:11
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 获取分类列表需求：
     * 1、只查询有发布正式文章的分类
     * 2、必须是正常状态的分类
     * */
    @Override
    public ResponseResult getCategory() {
        List<CategoryVo> categoryVos=null;
        if(redisCache.hasKey("lizi_blog_categorylist")){
            categoryVos=redisCache.getCacheList("lizi_blog_categorylist");
        }
        else{
            LambdaQueryWrapper<Article> articleLambdaQueryWrapper=new LambdaQueryWrapper<>();
            articleLambdaQueryWrapper.select(Article::getCategoryId).eq(Article::getStatus, SystemConstant.ARTICLE_STATUS_NORMAL);
            List<Article> articleList = articleService.list(articleLambdaQueryWrapper);
            //获取文章的分类id，并且去重  (函数式编程)
            Set<Long> categoryIdList = articleList.stream()
                    .map(article -> article.getCategoryId())
                    .collect(Collectors.toSet());
            //查询分类表,状态正常的标签  函数式编程
            List<Category> categories = listByIds(categoryIdList);
            categories = categories.stream()
                    .filter(category -> SystemConstant.CATEGORY_STATUS_NOARMAL.equals(category.getStatus()))
                    .collect(Collectors.toList());
            //封装vo
            categoryVos = BeanCopyUtil.copyBeanList(categories, CategoryVo.class);
            redisCache.setCacheList("lizi_blog_categorylist",categoryVos);
        }
        return ResponseResult.okResult(categoryVos);
    }
}




