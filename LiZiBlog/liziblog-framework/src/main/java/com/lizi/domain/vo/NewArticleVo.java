package com.lizi.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewArticleVo {
    private Long id;
    //标题
    private String title;

    @JsonFormat(timezone = "GMT8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
