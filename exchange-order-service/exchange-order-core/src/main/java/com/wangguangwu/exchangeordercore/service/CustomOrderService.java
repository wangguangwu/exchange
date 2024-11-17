package com.wangguangwu.exchangeordercore.service;

import com.wangguangwu.exchange.entity.OrdersDO;
import com.wangguangwu.exchange.enums.Direction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangguangwu
 */
public interface CustomOrderService {

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param sequenceId 定序ID
     * @param direction 订单方向
     * @param price 订单价格
     * @param quantity 订单数量
     * @return 创建的订单实体
     */
    OrdersDO createOrder(Long userId, Long sequenceId, Direction direction, BigDecimal price, BigDecimal quantity);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);

    /**
     * 获取用户的活动订单
     *
     * @param userId 用户ID
     * @return 活动订单列表
     */
    List<OrdersDO> getUserActiveOrders(Long userId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单实体
     */
    OrdersDO getOrderDetail(Long orderId);

}
