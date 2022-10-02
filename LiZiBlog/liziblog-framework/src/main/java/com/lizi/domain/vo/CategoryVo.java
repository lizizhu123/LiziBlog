package com.lizi.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CategoryVo {

    @ApiModelProperty(value = "标签id",example = "1")
    private Long id;
    @ApiModelProperty(value = "标签名称",example = "JAVA")
    private String name;
}
