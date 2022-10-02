package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Article;
import com.lizi.domain.entity.Category;
import com.lizi.domain.vo.*;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.mapper.ArticleMapper;
import com.lizi.service.ArticleService;
import com.lizi.service.CategoryService;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.RedisCache;
import com.lizi.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文章表(Article)表服务实现类
 *
 * @author makejava
 * @since 2022-09-12 11:17:05
 */
@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    @Lazy
    private CategoryService categoryService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取热门文章列表需求
     * 1.获取已发布的文章
     * 2.只能获取前十篇文章
     * 3.按浏览量进行排序
     *
     * @param pageNum
     * @param pageSize
     */
    @Override
    public ResponseResult gethotArticleList(Integer pageNum, Integer pageSize) {
        //查询热门文章 封装成ResponseResult返回
        /**
         * 需求：必须是正式文章  按照浏览量进行排序  最多只能查找10条记录
         * 1.数据库操作使用MyBatis-plus的方法
         *      LambdaQueryWrapper、eq、orderByDesc、page(Page对象，查询结果)
         *          page.getRecords():分页结果
         *
         * */
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstant.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);
        List<Article> articles = page.getRecords();
        /** bean拷贝对查询的字段限定
         * 将vo对象拷贝bean
         * 不推荐使用
         * */
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for (Article article : articles) {
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article, vo);
//            articleVos.add(vo);
//        }
        /**
         * 2.通过自己手写的SQL语句直接查询所需要的字段
         * 不需要进行bean拷贝
         * 不推荐使用
         * */
//        articleList = articleMapper.hotArticle();

        /**
         * 封装的工具类BeanCopyUtil
         * 使用静态方法进行bean拷贝
         * 使用stream进行类型转换
         * 推荐使用
         * */
        List<HotArticleVo> articleVos = BeanCopyUtil.copyBeanList(articles, HotArticleVo.class);
//
        //将结果封装成ResponseResult
        return ResponseResult.okResult(articleVos);
    }

    /**
     * 分页查询分类文章需求(同时满足首页热门文章展示)
     * 1.查询条件：
     * 如果 有categoryId!=0 就要查询时传入相同的分类id
     * categroyId:
     * -1:查询最新文章列表
     * 0:查询最热文章列表
     * >0:查询分类文章列表
     * 状态是已发布的文章
     * 对isTop进行判断是否执行置顶
     * 2.分页查询
     */
    @Override
    public ResponseResult getArticleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果 有categoryId查询分类文章
        if (categoryId != null) {
            if (categoryId > 0) {
                lambdaQueryWrapper.eq(categoryId > 0, Article::getCategoryId, categoryId);
                lambdaQueryWrapper.orderByDesc(Article::getViewCount);
                if (pageNum == 1) {
                    lambdaQueryWrapper.orderByDesc(Article::getIsTop);
                }
            }
            //categoryId==0  按照浏览量查询
            if (categoryId == 0) {
                lambdaQueryWrapper.orderByDesc(Article::getViewCount);
            }
            //如果categoryId==-1,则根据时间排序查询最新文章
            if (categoryId == -1) {
                lambdaQueryWrapper.orderByDesc(Article::getCreateTime);
            }
        }
        //状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstant.ARTICLE_STATUS_NORMAL);
        //只有第一页对IsTop降序排序

        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);

        //查询categoryName
        List<Article> records = page.getRecords();
        //categoryId去查询categoryName
        records.parallelStream().forEach(article -> article.setCategoryName(
                categoryService.getById(article.getCategoryId()).getName()));

        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtil.copyBeanList(page.getRecords(), ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        System.out.println(page.getTotal());

        return ResponseResult.okResult(pageVo);
    }


    /**
     * 根据id查询文章详情
     * 1.根据文章id查询文章详情
     * 2，根据分类id查询分类名
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {
        ArticleDetailVo articleDetailVo = null;

//        if(!redisCache.hasKey(SystemConstant.ARTICLE_DETAIL_KEY + String.valueOf(id))){
        //根据id查询文章
        System.out.println("查表");
        Article article = getById(id);
        //将结果转换成vo
        articleDetailVo = BeanCopyUtil.copyBean(article, ArticleDetailVo.class);
        //根据分类id查分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        System.out.println("-------------------------------------" + category);
        if (category != null) {
            articleDetailVo.setCategoryName(category.getName());
        }
//            redisCache.setCacheObject(SystemConstant.ARTICLE_DETAIL_KEY + String.valueOf(id),articleDetailVo);
//        }
//        else{
//            articleDetailVo= redisCache.getCacheObject(SystemConstant.ARTICLE_DETAIL_KEY + String.valueOf(id));
//            System.out.println("-------------"+articleDetailVo.getCategoryName());
//        }
        //封装结果

        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 获取最新文章
     * 1.状态为已发布
     * 2.根据时间排序
     * 3.最多只返回10条数据
     *
     * @param pageNum
     * @param pageSize
     */
    @Override
    public ResponseResult getNewArticle(Integer pageNum, Integer pageSize) {
//        if(redisCache.)
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstant.ARTICLE_STATUS_NORMAL);
        lambdaQueryWrapper.orderByAsc(Article::getCreateTime);
        Page<Article> page = new Page(pageNum, pageSize);
        page(page, lambdaQueryWrapper);
        List<Article> records = page.getRecords();
        List<NewArticleVo> newArticleVos = BeanCopyUtil.copyBeanList(records, NewArticleVo.class);
        return ResponseResult.okResult(newArticleVos);
    }

    @Override
    public ResponseResult addViewCount(Long id, String catoreyName) {


//       redisCache.deleteObject(SystemConstant.ARTICLE_DETAIL_KEY+String.valueOf(id));
        Article article = getById(id);
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Article::getViewCount, String.valueOf(article.getViewCount() + 1))
                .eq(Article::getId, article.getId());
        try {
            update(lambdaUpdateWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        article.setViewCount(article.getViewCount() + 1);
        ArticleDetailVo articleDetailVo = BeanCopyUtil.copyBean(article, ArticleDetailVo.class);
        articleDetailVo.setCategoryName(catoreyName);
        redisCache.setCacheObject(SystemConstant.ARTICLE_DETAIL_KEY + String.valueOf(id), articleDetailVo);
        return ResponseResult.okResult(articleDetailVo);
    }

    public ResponseResult getCreatorInfo() {
        int total = count();
        //总文章数
        Map<String, Long> map = new HashMap<>();
        map.put("total", (long) total);
        Long id = null;

        try {
            id = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.okResult(AppHttpCodeEnum.NEED_LOGIN.getCode(), String.valueOf(total));
        }

        map.put("userId", id);

        if (total == 0) {
            map.put("myTotal", 0L);
            map.put("viewCount", 0L);
        } else {
            LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Article::getCreateBy, id);
            int myTotal = count(lambdaQueryWrapper);
            System.out.println("我的总文章" + myTotal);
            if (myTotal != 0) {
                //我的总浏览量
                Long viewcount = articleMapper.viewCount(id);
                map.put("myTotal", (long) myTotal);
                map.put("viewCount", viewcount);
            } else {
                map.put("myTotal", 0L);
                map.put("viewCount", 0L);
            }
        }
        //我的文章数
        return ResponseResult.okResult(map);
    }

}

