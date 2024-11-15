package com.wangguangwu.assetcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangguangwu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomAssetServiceImpl implements CustomAssetService {

    private final AssetMapper assetMapper;
    private final AssetTransactionMapper transactionMapper;
    private final AssetTypeMapper assetTypeMapper;

    @Override
    public List<AssetDTO> getUserAssets(Long userId) {
        log.info("查询用户资产，用户ID: {}", userId);
        List<AssetDO> assets = assetMapper.selectList(
                new QueryWrapper<AssetDO>().eq("user_id", userId).eq("is_deleted", false));
        return assets.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean adjustBalance(AdjustBalanceRequest request) {
        log.info("调整用户资产余额，请求: {}", request);
        AssetDO asset = assetMapper.selectOne(
                new QueryWrapper<AssetDO>().eq("user_id", request.getUserId()).eq("asset_type_id", request.getAssetTypeId()).eq("is_deleted", false));
        if (asset == null) {
            throw new AssetException("资产记录不存在");
        }

        BigDecimal newBalance = asset.getAvailableBalance().add(request.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AssetException("可用余额不足");
        }

        asset.setAvailableBalance(newBalance);
        boolean success = assetMapper.updateById(asset) > 0;

        if (success) {
            log.info("更新资产记录成功，用户ID: {}, 资产类型: {}", request.getUserId(), request.getAssetTypeId());
            AssetTransactionDO transaction = new AssetTransactionDO();
            transaction.setUserId(request.getUserId());
            transaction.setAssetTypeId(request.getAssetTypeId());
            transaction.setTransactionType(request.getAmount().compareTo(BigDecimal.ZERO) > 0 ? 1 : 2);
            transaction.setAmount(request.getAmount());
            transaction.setBalanceAfter(newBalance);
            transaction.setFrozenBalanceAfter(asset.getFrozenBalance());
            transactionMapper.insert(transaction);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean freezeBalance(FreezeRequest request) {
        log.info("冻结用户资产，请求: {}", request);
        AssetDO asset = assetMapper.selectOne(
                new QueryWrapper<AssetDO>().eq("user_id", request.getUserId()).eq("asset_type_id", request.getAssetTypeId()).eq("is_deleted", false));
        if (asset == null) {
            throw new AssetException("资产记录不存在");
        }

        if (asset.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new AssetException("可用余额不足");
        }

        asset.setAvailableBalance(asset.getAvailableBalance().subtract(request.getAmount()));
        asset.setFrozenBalance(asset.getFrozenBalance().add(request.getAmount()));
        return assetMapper.updateById(asset) > 0;
    }

    @Override
    @Transactional
    public boolean unfreezeBalance(FreezeRequest request) {
        log.info("解冻用户资产，请求: {}", request);
        AssetDO asset = assetMapper.selectOne(
                new QueryWrapper<AssetDO>().eq("user_id", request.getUserId()).eq("asset_type_id", request.getAssetTypeId()).eq("is_deleted", false));
        if (asset == null) {
            throw new AssetException("资产记录不存在");
        }

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
        List<AssetTypeDO> types = assetTypeMapper.selectList(new QueryWrapper<AssetTypeDO>().eq("is_deleted", false));
        return types.stream().map(this::mapTypeToDTO).collect(Collectors.toList());
    }

    private AssetDTO mapToDTO(AssetDO asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);
        return dto;
    }

    private AssetTypeDTO mapTypeToDTO(AssetTypeDO type) {
        AssetTypeDTO dto = new AssetTypeDTO();
        BeanUtils.copyProperties(type, dto);
        return dto;
    }
}
