package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Message;
import com.lizi.domain.entity.User;
import com.lizi.domain.vo.MessageVo;
import com.lizi.domain.vo.PageVo;
import com.lizi.domain.vo.UserEasyVo;
import com.lizi.enums.AppHttpCodeEnum;
import com.lizi.exception.SystemException;
import com.lizi.mapper.UserMapper;
import com.lizi.service.MessageService;
import com.lizi.mapper.MessageMapper;
import com.lizi.service.UserService;
import com.lizi.utils.BeanCopyUtil;
import com.lizi.utils.SecurityUtils;
import org.apache.catalina.security.SecurityUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HUAWEI
* @description 针对表【lz_message(留言表)】的数据库操作Service实现
* @createDate 2022-09-25 20:42:48
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult addMessage(Message message) {
        Long id=null;
        try {
            id= SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            message.setCreateBy(-1L);
            try {
                save(message);
            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("保存失败"+message);
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
            }
            System.out.println("匿名发送"+message);
            return ResponseResult.okResult(message);
        }
        message.setCreateBy(id);
        save(message);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMessageList(Integer pageNum, Integer pageSize) {
        //分页查询留言列表
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Message::getCreateTime);
        page(page,lambdaQueryWrapper);
        //将列表转化为MessageVo
        List<MessageVo> messageVos = BeanCopyUtil.copyBeanList(page.getRecords(), MessageVo.class);
        //根据createBy查询用户简要信息
        messageVos.stream().forEach(messageVo -> {
            //如果createBy==-1 为匿名用户
            if(messageVo.getCreateBy()==-1){
                messageVo.setAvatar(SystemConstant.UNKNOWN_AVATAR);
                messageVo.setNickName(SystemConstant.UNKNOWN_NICKNAME);
            }
            else if(messageVo.getCreateBy()>0){
                User user = userService.getById(messageVo.getCreateBy());
                messageVo.setAvatar(user.getAvatar());
                messageVo.setNickName(user.getNickName());
            }
        });
        return ResponseResult.okResult(new PageVo(messageVos,page.getTotal()));
    }
}




