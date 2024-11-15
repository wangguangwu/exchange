package com.wangguangwu.exchange.dto;

import lombok.Data;

/**
 * @author wangguangwu
 */
@Data
public class AssetTypeDTO {

    private Long id;
    private String name;
    private String symbol;
    private int precision;

}
