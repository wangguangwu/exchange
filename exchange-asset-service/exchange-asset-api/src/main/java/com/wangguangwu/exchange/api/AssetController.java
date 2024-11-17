package com.wangguangwu.exchange.api;

import com.wangguangwu.exchange.dto.AdjustBalanceRequest;
import com.wangguangwu.exchange.dto.AssetDTO;
import com.wangguangwu.exchange.dto.AssetTypeDTO;
import com.wangguangwu.exchange.dto.FreezeRequest;
import com.wangguangwu.exchange.response.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wangguangwu
 */
@RequestMapping("/asset")
public interface AssetController {

    /**
     * 获取用户资产
     *
     * @param userId 用户ID
     * @return 用户资产列表
     */
    @GetMapping("/get/{userId}")
    Response<List<AssetDTO>> getUserAssets(@PathVariable("userId") Long userId);

    /**
     * 调整资产余额
     *
     * @param request 调整资产请求
     * @return 调整结果
     */
    @PostMapping("/adjust")
    Response<String> adjustBalance(@RequestBody AdjustBalanceRequest request);

    /**
     * 冻结用户资产
     *
     * @param request 冻结资产请求
     * @return 冻结结果
     */
    @PostMapping("/freeze")
    Response<String> freezeBalance(@RequestBody FreezeRequest request);

    /**
     * 解冻用户资产
     *
     * @param request 解冻资产请求
     * @return 解冻结果
     */
    @PostMapping("/unfreeze")
    Response<String> unfreezeBalance(@RequestBody FreezeRequest request);

    /**
     * 获取资产类型列表
     *
     * @return 资产类型列表
     */
    @GetMapping("listAssetTypes")
    Response<List<AssetTypeDTO>> listAssetTypes();
}
