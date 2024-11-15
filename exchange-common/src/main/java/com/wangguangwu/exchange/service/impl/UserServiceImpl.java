package com.wangguangwu.exchange.service.impl;

import com.wangguangwu.exchange.entity.UserDO;
import com.wangguangwu.exchange.mapper.UserMapper;
import com.wangguangwu.exchange.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

}
