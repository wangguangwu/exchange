package com.wangguangwu.exchange.api;

import com.wangguangwu.exchange.dto.OrderDTO;
import com.wangguangwu.exchange.request.CancelOrderRequest;
import com.wangguangwu.exchange.request.CreateOrderRequest;
import com.wangguangwu.exchange.response.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wangguangwu
 */
@RequestMapping("/order")
public interface OrderController {

    /**
     * 创建订单
     *
     * @param request 创建订单请求
     * @return 创建结果
     */
    @PostMapping("/create")
    Response<OrderDTO> createOrder(@RequestBody CreateOrderRequest request);

    /**
     * 取消订单
     *
     * @param request 取消订单请求
     * @return 取消结果
     */
    @PostMapping("/cancel")
    Response<String> cancelOrder(@RequestBody CancelOrderRequest request);

    /**
     * 获取用户的活动订单
     *
     * @param userId 用户ID
     * @return 活动订单列表
     */
    @GetMapping("/active/{userId}")
    Response<List<OrderDTO>> getUserActiveOrders(@PathVariable("userId") Long userId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    Response<OrderDTO> getOrderDetail(@PathVariable("orderId") Long orderId);
}
