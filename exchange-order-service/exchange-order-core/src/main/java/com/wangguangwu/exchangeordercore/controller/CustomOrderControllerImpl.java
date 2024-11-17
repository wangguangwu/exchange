package com.wangguangwu.exchangeordercore.controller;

import com.wangguangwu.exchange.api.OrderController;
import com.wangguangwu.exchange.dto.OrderDTO;
import com.wangguangwu.exchange.entity.OrdersDO;
import com.wangguangwu.exchange.enums.Direction;
import com.wangguangwu.exchange.request.CancelOrderRequest;
import com.wangguangwu.exchange.request.CreateOrderRequest;
import com.wangguangwu.exchange.response.Response;
import com.wangguangwu.exchangeordercore.service.CustomOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangguangwu
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomOrderControllerImpl implements OrderController {

    private final CustomOrderService orderService;


    /**
     * 创建订单
     *
     * @param request 创建订单请求
     * @return 创建结果
     */
    @Override
    public Response<OrderDTO> createOrder(CreateOrderRequest request) {
        // 调用服务层逻辑创建订单
        OrdersDO order = orderService.createOrder(
                request.getUserId(),
                request.getSequenceId(),
                Direction.fromCode(request.getDirection()),
                request.getPrice(),
                request.getQuantity()
        );
        return Response.success(convertToDTO(order));
    }

    /**
     * 取消订单
     *
     * @param request 取消订单请求
     * @return 取消结果
     */
    @Override
    public Response<String> cancelOrder(CancelOrderRequest request) {
        orderService.cancelOrder(request.getOrderId());
        return Response.success("订单取消成功。");
    }

    /**
     * 获取用户的活动订单
     *
     * @param userId 用户ID
     * @return 活动订单列表
     */
    @Override
    public Response<List<OrderDTO>> getUserActiveOrders(Long userId) {
        List<OrdersDO> activeOrders = orderService.getUserActiveOrders(userId);
        List<OrderDTO> orderDTOs = activeOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Response.success(orderDTOs);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public Response<OrderDTO> getOrderDetail(Long orderId) {
        OrdersDO order = orderService.getOrderDetail(orderId);
        return Response.success(convertToDTO(order));
    }

    /**
     * 将实体类转换为 DTO
     *
     * @param entity 订单实体类
     * @return 转换后的 DTO
     */
    private OrderDTO convertToDTO(OrdersDO entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setSequenceId(entity.getSequenceId());
        dto.setDirection(entity.getDirection());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setUnfilledQuantity(entity.getUnfilledQuantity());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
