package com.wangguangwu.exchangeordercore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wangguangwu.exchange.entity.OrdersDO;
import com.wangguangwu.exchange.enums.Direction;
import com.wangguangwu.exchange.enums.OrderStatus;
import com.wangguangwu.exchange.mapper.OrdersMapper;
import com.wangguangwu.exchange.service.OrdersService;
import com.wangguangwu.exchangeordercore.service.CustomOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOrderServiceImpl implements CustomOrderService {

    private final OrdersService ordersService;
    private final OrdersMapper ordersMapper;

    /**
     * 创建订单
     *
     * @param userId     用户ID
     * @param sequenceId 定序ID
     * @param direction  订单方向（BUY 或 SELL）
     * @param price      订单价格
     * @param quantity   订单数量
     * @return 创建的订单实体
     */
    @Override
    public OrdersDO createOrder(Long userId, Long sequenceId, Direction direction, BigDecimal price, BigDecimal quantity) {
        log.info("创建订单: userId={}, sequenceId={}, direction={}, price={}, quantity={}",
                userId, sequenceId, direction, price, quantity);

        // 构建订单数据对象
        OrdersDO order = new OrdersDO();
        order.setUserId(userId);
        order.setSequenceId(sequenceId);
        order.setDirection(direction.getCode());
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setUnfilledQuantity(quantity);
        order.setStatus(OrderStatus.WAITING.getDescription());
        order.setCreatedAt(System.currentTimeMillis());
        order.setUpdatedAt(System.currentTimeMillis());

        // 保存订单到数据库
        ordersMapper.insert(order);
        log.info("订单创建成功: {}", order);
        return order;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    @Override
    public void cancelOrder(Long orderId) {
        log.info("取消订单: orderId={}", orderId);

        // 查询订单
        OrdersDO order = ordersMapper.selectById(orderId);
        if (order == null) {
            log.error("订单不存在，无法取消: orderId={}", orderId);
            throw new IllegalArgumentException("订单不存在: " + orderId);
        }

        // 检查订单状态
        // TODO： 能不能进行优化
        if (OrderStatus.COMPLETE.getDescription().equals(order.getStatus())) {
            log.error("订单已完成，无法取消: {}", order);
            throw new IllegalStateException("订单已完成，无法取消");
        }

        // 更新订单状态为取消
        order.setStatus(OrderStatus.CANCELLED.getDescription());
        order.setUpdatedAt(System.currentTimeMillis());
        ordersMapper.updateById(order);

        log.info("订单取消成功: {}", order);
    }

    /**
     * 获取用户的活动订单
     *
     * @param userId 用户ID
     * @return 活动订单列表
     */
    @Override
    public List<OrdersDO> getUserActiveOrders(Long userId) {
        log.info("获取用户活动订单: userId={}", userId);

        // 使用 LambdaQueryWrapper 查询活动订单
        List<OrdersDO> activeOrders = ordersService.list(
                new LambdaQueryWrapper<OrdersDO>()
                        .eq(OrdersDO::getUserId, userId)
                        .in(OrdersDO::getStatus,
                                OrderStatus.WAITING.getDescription(),
                                OrderStatus.PARTIAL.getDescription()
                        )
        );

        log.info("活动订单查询成功: userId={}, activeOrders={}", userId, activeOrders.size());
        return activeOrders;
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public OrdersDO getOrderDetail(Long orderId) {
        log.info("获取订单详情: orderId={}", orderId);

        // 查询订单详情
        OrdersDO order = ordersMapper.selectById(orderId);
        if (order == null) {
            log.error("订单不存在: orderId={}", orderId);
            throw new IllegalArgumentException("订单不存在: " + orderId);
        }

        log.info("订单详情查询成功: {}", order);
        return order;
    }
}
