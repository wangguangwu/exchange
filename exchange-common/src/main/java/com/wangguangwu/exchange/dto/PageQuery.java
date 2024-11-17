package com.wangguangwu.exchange.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author wangguangwu
 */
@Data
public class PageQuery {

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private int page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    private int size = 10;

}
