package com.wangguangwu.exchange.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangguangwu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderRequest {

    private Long orderId;

}
