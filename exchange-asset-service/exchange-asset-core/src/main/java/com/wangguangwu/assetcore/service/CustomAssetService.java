package com.wangguangwu.assetcore.service;

import com.wangguangwu.exchange.dto.AdjustBalanceRequest;
import com.wangguangwu.exchange.dto.AssetDTO;
import com.wangguangwu.exchange.dto.AssetTypeDTO;
import com.wangguangwu.exchange.dto.FreezeRequest;

import java.util.List;

/**
 * @author wangguangwu
 */
public interface CustomAssetService {

    /**
     * 获取用户资产列表
     *
     * @param userId 用户ID
     * @return 用户资产列表
     */
    List<AssetDTO> getUserAssets(Long userId);

    /**
     * 调整用户资产余额
     *
     * @param request 调整余额的请求
     * @return 调整是否成功
     */
    boolean adjustBalance(AdjustBalanceRequest request);

    /**
     * 冻结用户资产
     *
     * @param request 冻结请求
     * @return 冻结是否成功
     */
    boolean freezeBalance(FreezeRequest request);

    /**
     * 解冻用户资产
     *
     * @param request 解冻请求
     * @return 解冻是否成功
     */
    boolean unfreezeBalance(FreezeRequest request);

    /**
     * 获取资产类型列表
     *
     * @return 资产类型列表
     */
    List<AssetTypeDTO> listAssetTypes();

}
