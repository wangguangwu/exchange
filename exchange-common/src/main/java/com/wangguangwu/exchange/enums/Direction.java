package com.wangguangwu.exchange.enums;

import lombok.Getter;

/**
 * @author wangguangwu
 */
@Getter
public enum Direction {

    BUY(0, "Buy"),
    SELL(1, "Sell");

    private final int code;
    private final String description;

    Direction(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Direction fromCode(int code) {
        for (Direction direction : values()) {
            if (direction.code == code) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid direction code: " + code);
    }
}
