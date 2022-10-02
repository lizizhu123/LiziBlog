package com.lizi.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 根评论id
     */
    private Long rootId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 所回复的目标评论的userid
     */
    private Long toCommentUserId;

    /**
     * 回复的人昵称
     * */
    private String toCommentUserName;

    /**
     * 回复目标评论id
     */
    private Long toCommentId;

    /**
     *
     */
    private Long createBy;

    /***
     * 回复总条数
     */
    @TableField(exist = false)
    private Long commentTotal;
    /**
     *
     */
    @JsonFormat(timezone = "Asia/Shanghai",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 评论者昵称
     * */
    private String userName;

    /**
     * 评论者头像
     * */
    private String img;

    private Integer pageNum;
    private Integer pageSize;

}
