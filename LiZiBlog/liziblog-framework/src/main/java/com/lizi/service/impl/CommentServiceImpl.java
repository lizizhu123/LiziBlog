package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.CommentDto;
import com.lizi.domain.entity.Comment;
import com.lizi.domain.entity.User;
import com.lizi.domain.vo.CommentVo;
import com.lizi.domain.vo.PageVo;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.service.CommentService;
import com.lizi.mapper.CommentMapper;
import com.lizi.service.UserService;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.SecurityUtils;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HUAWEI
* @description 针对表【sg_comment(评论表)】的数据库操作Service实现
* @createDate 2022-09-20 11:07:31
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    @Lazy
    @Autowired
    private UserService userService;
    /**
     * 获取文章根评论  分页查询
     *
     * */
    @Override
    public ResponseResult getCommentList(String type, Long articleId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //评论类型==0?文章:友链
        lambdaQueryWrapper.eq(Comment::getType,type);

        //查询对应文章id的评论
        if(SystemConstant.ARTICLE_COMMENT_TYPE.equals(type)){
            lambdaQueryWrapper.eq(SystemConstant.ARTICLE_COMMENT_TYPE.equals(type),Comment::getArticleId,articleId);
        }
        //查询根评论
        lambdaQueryWrapper.eq(Comment::getRootId, SystemConstant.COMMENT_IS_ROOT);
//        lambdaQueryWrapper.orderByDesc(Comment::getCreateTime);
        //分页查询
        Page<Comment> page=new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);
        List<CommentVo> commentVos=toCommentVo(page.getRecords());
        //查询回复数量
        commentVos.stream().forEach((CommentVo commentVo) -> {
            commentVo.setContent(EmojiParser.parseToUnicode(commentVo.getContent()));
            LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
            query.eq(Comment::getRootId,commentVo.getId());
            //直接通过以上的两句代码实现条件查询计数，之后调用集成好的selectCount就ok了。
            commentVo.setCommentTotal(Long.valueOf(commentMapper.selectCount(query)));;
        });
        return ResponseResult.okResult(new PageVo(commentVos,page.getTotal()));
    }

    @Override
    public ResponseResult getChildrenCommentList(String linkCommentType, Long id, Integer pageNum, Integer pageSize){
        LambdaQueryWrapper<Comment> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getType,linkCommentType);
        lambdaQueryWrapper.eq(Comment::getRootId,id);
//        lambdaQueryWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page=new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);
        if(page.getTotal()>0){
            List<CommentVo> commentVos=toCommentVo(page.getRecords());
            commentVos.stream().forEach(commentVo ->
            {
                commentVo.setPageNum(pageNum);
                commentVo.setPageSize(pageSize);
                commentVo.setContent(EmojiParser.parseToUnicode(commentVo.getContent()));
            });

            return ResponseResult.okResult(new PageVo(commentVos,page.getTotal()));
        }
        else{
            return ResponseResult.errorResult(AppHttpCodeEnum.PAGE_TOTAL_NULL);
        }
    }

    @Override
    public ResponseResult getCommentToTal(Long articleId) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(articleId==-1){
            lambdaQueryWrapper.eq(Comment::getType,SystemConstant.LINK_COMMENT_TYPE);
        }else{
            lambdaQueryWrapper.eq(Comment::getArticleId,articleId);
            lambdaQueryWrapper.eq(Comment::getType,SystemConstant.ARTICLE_COMMENT_TYPE);
        }
        Integer total = commentMapper.selectCount(lambdaQueryWrapper);
        if(total>0){
            return ResponseResult.okResult(total);
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.PAGE_TOTAL_NULL);
        }
    }

    @Override
    public ResponseResult addComment(CommentDto comment) {
        Long id= null;
        try {
            id = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        comment.setCreateBy(id);
//        comment.setContent(EmojiParser.parseToAliases(comment.getContent(), EmojiParser.FitzpatrickAction.PARSE));
        save(comment);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult delComment(CommentDto comment) {
        if(comment.getId()==null){
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        else{
            int i=commentMapper.deleteById(comment.getId());
            if(i>0){
                return ResponseResult.okResult();
            }
            else{
                return ResponseResult.errorResult(AppHttpCodeEnum.PAGE_TOTAL_NULL);
            }
        }

    }


    private List<CommentVo> toCommentVo(List<Comment> list){
        List<CommentVo> commentVos= BeanCopyUtil.copyBeanList(list,CommentVo.class);
        //设置评论者昵称、头像
        commentVos.stream().forEach(commentVo ->
                {
                    User user=userService.getById(commentVo.getCreateBy());
                    commentVo.setUserName(user.getNickName());
                    commentVo.setImg(user.getAvatar());
                    if(commentVo.getRootId()!=-1){
                        commentVo.setToCommentUserName(userService.getById(commentVo.getToCommentUserId()).getNickName());
                    }
                }
        );
        return commentVos;

    }


}




