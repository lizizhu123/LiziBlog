package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.CommentDto;
import com.lizi.domain.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HUAWEI
* @description 针对表【sg_comment(评论表)】的数据库操作Service
* @createDate 2022-09-20 11:07:31
*/
public interface CommentService extends IService<Comment> {

    ResponseResult getCommentList(String type, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult getChildrenCommentList(String linkCommentType, Long id, Integer pageNum, Integer pageSize);

    ResponseResult getCommentToTal(Long articleId);

    ResponseResult addComment(CommentDto comment);

    ResponseResult delComment(CommentDto comment);
}
