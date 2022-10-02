package com.lizi.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ArticleListVo {
    @ApiModelProperty(value = "文章id",example = "1")
    private Long id;
    //标题
    @ApiModelProperty(value = "文章标题",example = "SpringMVC")
    private String title;
    //文章摘要
    @ApiModelProperty(value = "文章摘要",example = "详细解释SpringMVC原理")
    private String summary;
    //所属分类id
    private Long categoryId;
    //所属分类名
    @TableField(exist = false)
    @ApiModelProperty(value = "标签名称",example = "JAVA")
    private String categoryName;
    //缩略图
    @ApiModelProperty(value = "缩略图",example = "http:xxx.com/xxx.jpg")
    private String thumbnail;
//    //是否置顶（0否，1是）
//    @ApiModelProperty(value = "是否置顶",example = "1")
//    private String isTop;
    //访问量
    @ApiModelProperty(value = "访问量",example = "439")
    private Long viewCount;

    @ApiModelProperty(value = "创作者",example = "栗子猪")
    private Long createBy;

    @JsonFormat(timezone = "GMT8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发布时间",example = "2022-09-14 14:46:32")
    private Date createTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
}
