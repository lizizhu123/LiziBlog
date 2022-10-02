package com.lizi.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatorVo {
    private Long userId;
    private int total;
    private int myTotal;
    private Long viewCount;

}
