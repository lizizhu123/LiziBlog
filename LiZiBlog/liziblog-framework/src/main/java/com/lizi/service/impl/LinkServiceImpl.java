package com.lizi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizi.constants.SystemConstant;
import com.lizi.domain.ResponseResult;
import com.lizi.domain.entity.Link;
import com.lizi.domain.vo.LinkVo;
import com.lizi.domain.vo.PageVo;
import com.lizi.service.LinkService;
import com.lizi.mapper.LinkMapper;
import com.lizi.utils.BeanCopyUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HUAWEI
* @description 针对表【sg_link(友链)】的数据库操作Service实现
* @createDate 2022-09-14 22:01:56
*/
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link>
    implements LinkService{

    /**
     * 获取所有友链信息
     * 1.查询所有状态为审核通过的友链
     * */
    @Override
    public ResponseResult getAllLink(Integer pageNum,Integer pageSize) {
        LambdaQueryWrapper<Link> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Link::getStatus, SystemConstant.LINK_STATUS_NORMAL);

        Page<Link> page=new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);
        PageVo pageVo = new PageVo(BeanCopyUtil.copyBeanList(page.getRecords(), LinkVo.class), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
}




