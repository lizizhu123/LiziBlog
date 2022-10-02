package com.lizi.constants;

import com.lizi.domain.vo.LinkVo;

public class SystemConstant {
    /**
     * 文章为草稿
     * */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     * 文章为正常发布
     * */
    public static final int ARTICLE_STATUS_NORMAL = 0;
    /**
     * 文章分页查询起始
     * */
    public static final int ARTICLE_PAGE_START = 0;
    /**
     * 文章分页查询结束
     * */
    public static final int ARTICLE_PAGE_END = 10;
    /**
     * 分类正常状态
     * */
    public static final String CATEGORY_STATUS_NOARMAL="0";
    /**
     * 友链审核通过
     * */
    public static final String LINK_STATUS_NORMAL="0";
    /**
     * 登陆Redis中的key
     * */
    public static final String BLOG_LOGIN_REDIS_KEY="bloglogin";

    /**
     * 请求头中存储token的key
     * */
    public static final String REQUEST_HEADER_TOKEN_KEY="lizi_blog_token";

    /**
     * 文章详情存储在redis中的key
     * */
    public static final String ARTICLE_DETAIL_KEY="lizi_blog_article_id";
    /**
     * 最新文章列表redis中的key
     */
    public static final String NEW_ARTICLE_LIST_KEY="lizi_blog_new_article";
    /**
     * 评论为根评论：-1
     * */
    public static final String COMMENT_IS_ROOT="-1";
    /**
     * 文章评论标志：0
     * */
    public static final String ARTICLE_COMMENT_TYPE="0";
    /**
     * 友链评论标志：1
     * */
    public static final String LINK_COMMENT_TYPE="1";

    /**
     * 匿名用户头像地址
     */
    public static final String UNKNOWN_AVATAR="http://www.lizizhucloud.com/player/img/unknow.jpg";

    /**
     * 匿名用户名称
     */
    public static final String UNKNOWN_NICKNAME="匿名用户";

    /**
     * 短信验证码redis  key
     * */
    public static final String SMS_REIDS_KEY="lizi_blog_redis";

}
