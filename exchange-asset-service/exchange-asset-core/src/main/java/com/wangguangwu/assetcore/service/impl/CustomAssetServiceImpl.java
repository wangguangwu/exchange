package com.wangguangwu.assetcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wangguangwu.assetcore.service.CustomAssetService;
import com.wangguangwu.exchange.dto.AdjustBalanceRequest;
import com.wangguangwu.exchange.dto.AssetDTO;
import com.wangguangwu.exchange.dto.AssetTypeDTO;
import com.wangguangwu.exchange.dto.FreezeRequest;
import com.wangguangwu.exchange.entity.AssetDO;
import com.wangguangwu.exchange.entity.AssetTransactionDO;
import com.wangguangwu.exchange.entity.AssetTypeDO;
import com.wangguangwu.exchange.exception.AssetException;
import com.wangguangwu.exchange.mapper.AssetMapper;
import com.wangguangwu.exchange.mapper.AssetTransactionMapper;
import com.wangguangwu.exchange.mapper.AssetTypeMapper;
import com.wangguangwu.exchange.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAssetServiceImpl implements CustomAssetService {

    private final AssetMapper assetMapper;
    private final AssetTransactionMapper transactionMapper;
    private final AssetTypeMapper assetTypeMapper;

    @Override
    public List<AssetDTO> getUserAssets(Long userId) {
        log.info("查询用户资产，用户ID: {}", userId);
        List<AssetDO> assets = assetMapper.selectList(buildAssetQuery(userId, null));
        return MappingUtils.mapList(assets, AssetDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustBalance(AdjustBalanceRequest request) {
        log.info("调整用户资产余额，请求: {}", request);
        AssetDO asset = getAssetOrThrow(request.getUserId(), request.getAssetTypeId());

        BigDecimal newBalance = asset.getAvailableBalance().add(request.getAmount());
        validateBalance(newBalance);

        asset.setAvailableBalance(newBalance);
        boolean success = assetMapper.updateById(asset) > 0;

        if (success) {
            logTransaction(request, asset, newBalance);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeBalance(FreezeRequest request) {
        log.info("冻结用户资产，请求: {}", request);
        AssetDO asset = getAssetOrThrow(request.getUserId(), request.getAssetTypeId());

        if (asset.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new AssetException("可用余额不足");
        }

        asset.setAvailableBalance(asset.getAvailableBalance().subtract(request.getAmount()));
        asset.setFrozenBalance(asset.getFrozenBalance().add(request.getAmount()));
        return assetMapper.updateById(asset) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeBalance(FreezeRequest request) {
        log.info("解冻用户资产，请求: {}", request);
        AssetDO asset = getAssetOrThrow(request.getUserId(), request.getAssetTypeId());

        if (asset.getFrozenBalance().compareTo(request.getAmount()) < 0) {
            throw new AssetException("冻结余额不足");
        }

        asset.setFrozenBalance(asset.getFrozenBalance().subtract(request.getAmount()));
        asset.setAvailableBalance(asset.getAvailableBalance().add(request.getAmount()));
        return assetMapper.updateById(asset) > 0;
    }

    @Override
    public List<AssetTypeDTO> listAssetTypes() {
        log.info("查询资产类型列表");
        List<AssetTypeDO> types = assetTypeMapper.selectList(
                new LambdaQueryWrapper<AssetTypeDO>().eq(AssetTypeDO::getIsDeleted, false));
        return MappingUtils.mapList(types, AssetTypeDTO.class);
    }

    // ========================= 私有方法 =========================

    private LambdaQueryWrapper<AssetDO> buildAssetQuery(Long userId, Long assetTypeId) {
        LambdaQueryWrapper<AssetDO> query = new LambdaQueryWrapper<>();
        query.eq(AssetDO::getUserId, userId).eq(AssetDO::getIsDeleted, false);
        if (assetTypeId != null) {
            query.eq(AssetDO::getAssetTypeId, assetTypeId);
        }
        return query;
    }

    private AssetDO getAssetOrThrow(Long userId, Long assetTypeId) {
        AssetDO asset = assetMapper.selectOne(buildAssetQuery(userId, assetTypeId));
        if (asset == null) {
            throw new AssetException("资产记录不存在");
        }
        return asset;
    }

    private void validateBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AssetException("可用余额不足");
        }
    }

    private void logTransaction(AdjustBalanceRequest request, AssetDO asset, BigDecimal newBalance) {
        AssetTransactionDO transaction = new AssetTransactionDO();
        transaction.setUserId(request.getUserId());
        transaction.setAssetTypeId(request.getAssetTypeId());
        transaction.setTransactionType(request.getAmount().compareTo(BigDecimal.ZERO) > 0 ? 1 : 2);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setFrozenBalanceAfter(asset.getFrozenBalance());
        transactionMapper.insert(transaction);
        log.info("资产交易记录已插入，用户ID: {}, 资产类型: {}", request.getUserId(), request.getAssetTypeId());
    }
}
