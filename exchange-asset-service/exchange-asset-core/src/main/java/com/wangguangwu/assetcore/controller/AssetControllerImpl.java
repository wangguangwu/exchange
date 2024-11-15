package com.wangguangwu.assetcore.controller;

import com.wangguangwu.assetcore.service.CustomAssetService;
import com.wangguangwu.exchange.api.AssetController;
import com.wangguangwu.exchange.dto.AdjustBalanceRequest;
import com.wangguangwu.exchange.dto.AssetDTO;
import com.wangguangwu.exchange.dto.FreezeRequest;
import com.wangguangwu.exchange.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangguangwu
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AssetControllerImpl implements AssetController {

    private final CustomAssetService assetService;

    @Override
    public Response<List<AssetDTO>> getUserAssets(@PathVariable("userId") Long userId) {
        try {
            log.info("开始获取用户资产，用户ID: {}", userId);
            List<AssetDTO> assets = assetService.getUserAssets(userId);
            log.info("成功获取用户资产，用户ID: {}, 资产列表: {}", userId, assets);
            return Response.success(assets);
        } catch (Exception e) {
            log.error("获取用户资产失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            return Response.error("获取用户资产失败");
        }
    }

    @Override
    public Response<String> adjustBalance(@RequestBody AdjustBalanceRequest request) {
        try {
            log.info("开始调整资产余额，请求: {}", request);
            boolean success = assetService.adjustBalance(request);
            log.info("资产余额调整结果，请求: {}, 成功: {}", request, success);
            return success ? Response.success("资产余额调整成功") : Response.error("资产余额调整失败");
        } catch (Exception e) {
            log.error("调整资产余额失败，请求: {}, 错误信息: {}", request, e.getMessage(), e);
            return Response.error("资产余额调整失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> freezeBalance(@RequestBody FreezeRequest request) {
        try {
            log.info("开始冻结资产，请求: {}", request);
            boolean success = assetService.freezeBalance(request);
            log.info("资产冻结结果，请求: {}, 成功: {}", request, success);
            return success ? Response.success("资产冻结成功") : Response.error("资产冻结失败");
        } catch (Exception e) {
            log.error("冻结资产失败，请求: {}, 错误信息: {}", request, e.getMessage(), e);
            return Response.error("冻结资产失败: " + e.getMessage());
        }
    }

    @Override
    public Response<String> unfreezeBalance(@RequestBody FreezeRequest request) {
        try {
            log.info("开始解冻资产，请求: {}", request);
            boolean success = assetService.unfreezeBalance(request);
            log.info("资产解冻结果，请求: {}, 成功: {}", request, success);
            return success ? Response.success("资产解冻成功") : Response.error("资产解冻失败");
        } catch (Exception e) {
            log.error("解冻资产失败，请求: {}, 错误信息: {}", request, e.getMessage(), e);
            return Response.error("资产解冻失败: " + e.getMessage());
        }
    }
}