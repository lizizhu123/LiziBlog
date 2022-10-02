package com.lizi.controller;

import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.CommentDto;
import com.lizi.domain.entity.Comment;
import com.lizi.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags="评论")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("获取评论")
    @GetMapping("/commentlist")
    public ResponseResult getCommentList(Long articleId,Integer pageNum,Integer pageSize){
        return commentService.getCommentList(SystemConstant.ARTICLE_COMMENT_TYPE, articleId,pageNum,pageSize);
    }
    @ApiOperation("获取回复列表")
    @GetMapping("/childerncommentlist")
    public ResponseResult getChildrenList(Long rootId,Integer pageNum,Integer pageSize){
        return commentService.getChildrenCommentList(SystemConstant.ARTICLE_COMMENT_TYPE,rootId,pageNum,pageSize);
    }

    @ApiOperation("获取文章评论总数")
    @GetMapping("/commenttotal")
    public ResponseResult getCommentToTal(Long articleId){
        return commentService.getCommentToTal(articleId);
    }

    @PostMapping
    @ApiOperation("发表评论")
    public ResponseResult addcomment(@RequestBody CommentDto comment){
        if(comment.getArticleId()!=null){
            return commentService.addComment(comment);
        }else{
            return ResponseResult.okResult();
        }
    }

    @ApiOperation("删除评论")
    @PostMapping("/deletecomment")
    public ResponseResult delComment(@RequestBody CommentDto comment){
        return commentService.delComment(comment);
    }

    @ApiOperation("获取友链评论")
    @GetMapping("linkcommentlist")
    public ResponseResult getLinkCommentList(Long articleId,Integer pageNum,Integer pageSize){
        return commentService.getCommentList(SystemConstant.LINK_COMMENT_TYPE,null,pageNum,pageSize);
    }

    @ApiOperation("获取友链回复列表")
    @GetMapping("/linkchildercommentlist")
    public ResponseResult getLinkChildrenList(Long rootId,Integer pageNum,Integer pageSize){
        return commentService.getChildrenCommentList(SystemConstant.LINK_COMMENT_TYPE,rootId,pageNum,pageSize);
    }
}
