package com.wangguangwu.exchange.enums;

import lombok.Getter;

/**
 * @author wangguangwu
 */
@Getter
public enum OrderStatus {

    WAITING("Waiting for execution"),
    PARTIAL("Partially executed"),
    COMPLETE("Fully executed"),
    CANCELLED("Order cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public static OrderStatus fromName(String name) {
        for (OrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + name);
    }
}
