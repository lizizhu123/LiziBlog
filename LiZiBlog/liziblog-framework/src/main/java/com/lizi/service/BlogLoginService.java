package com.lizi.service;

import com.lizi.domain.ResponseResult;
import com.lizi.domain.dto.UserDto;
import com.lizi.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(UserDto user);

    ResponseResult logout();
}
