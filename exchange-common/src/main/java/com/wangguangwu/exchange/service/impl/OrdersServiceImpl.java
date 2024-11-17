package com.wangguangwu.exchange.service.impl;

import com.wangguangwu.exchange.entity.OrdersDO;
import com.wangguangwu.exchange.mapper.OrdersMapper;
import com.wangguangwu.exchange.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-17
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, OrdersDO> implements OrdersService {

}
