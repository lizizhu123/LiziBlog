package com.lizi.domain.vo;

import com.lizi.domain.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVo extends Message {
    private String avatar;
    private String nickName;
}
