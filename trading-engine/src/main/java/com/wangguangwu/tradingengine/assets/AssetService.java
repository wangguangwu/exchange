package com.wangguangwu.tradingengine.assets;

import com.wangguangwu.exchange.enums.AssetEnum;
import com.wangguangwu.exchange.enums.LockType;
import com.wangguangwu.exchange.enums.Transfer;
import com.wangguangwu.exchange.exception.AssetException;
import com.wangguangwu.exchange.lockmanager.UserLockManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 资产系统服务
 * <p>
 * 提供资产的查询、转移、冻结和解冻等操作。
 *
 * @author wangguangwu
 */
@Component
@Getter
@Slf4j
public class AssetService {

    private final UserLockManager userLockManager;

    /**
     * 用户资产映射：用户ID -> (资产ID -> 资产对象)
     */
    private final ConcurrentMap<Long, ConcurrentMap<AssetEnum, Asset>> userAssets = new ConcurrentHashMap<>();

    @Autowired
    public AssetService(UserLockManager userLockManager) {
        this.userLockManager = userLockManager;
    }

    //=====================================公有方法=================================

    /**
     * 获取指定用户的某种资产对象。
     *
     * @param userId  用户 ID
     * @param assetId 资产类型的枚举值
     * @return 如果存在该资产，返回对应的 Asset；否则返回 null
     */
    public Asset getAsset(Long userId, AssetEnum assetId) {
        return userLockManager.executeWithLock(
                userLockManager.getLock(userId),
                LockType.READ,
                () -> userAssets.getOrDefault(userId, new ConcurrentHashMap<>()).get(assetId)
        );
    }

    /**
     * 获取指定用户的所有资产映射。
     *
     * @param userId 用户 ID
     * @return 如果用户没有资产，返回一个空的不可变 Map；否则返回用户的资产映射
     */
    public Map<AssetEnum, Asset> getAssets(Long userId) {
        return userLockManager.executeWithLock(
                userLockManager.getLock(userId),
                LockType.READ,
                () -> Collections.unmodifiableMap(userAssets.getOrDefault(userId, new ConcurrentHashMap<>()))
        );
    }

    /**
     * 增加用户资产的可用余额。
     *
     * @param userId  用户 ID
     * @param assetId 资产类型
     * @param amount  增加的金额
     */
    public void addAsset(Long userId, AssetEnum assetId, BigDecimal amount) {
        // 验证金额合法性
        validateAmount(amount, userId, assetId);

        userLockManager.executeWithLock(
                userLockManager.getLock(userId),
                LockType.WRITE,
                () -> {
                    Asset asset = initAssets(userId, assetId);
                    asset.setAvailable(asset.getAvailable().add(amount));
                }
        );
    }

    /**
     * 尝试冻结用户的可用资产。
     *
     * @param userId  用户 ID
     * @param assetId 资产类型的枚举值
     * @param amount  冻结金额
     * @return 如果冻结成功，返回 true；否则返回 false
     */
    public boolean tryFreeze(Long userId, AssetEnum assetId, BigDecimal amount) {
        return tryTransfer(Transfer.AVAILABLE_TO_FROZEN, userId, userId, assetId, amount, true);
    }

    /**
     * 解冻用户的冻结资产。
     *
     * @param userId  用户 ID
     * @param assetId 资产类型的枚举值
     * @param amount  解冻金额
     * @throws AssetException 如果解冻失败，抛出资产操作异常
     */
    public void unfreeze(Long userId, AssetEnum assetId, BigDecimal amount) {
        if (!tryTransfer(Transfer.FROZEN_TO_AVAILABLE, userId, userId, assetId, amount, true)) {
            throw new AssetException("Insufficient frozen balance for unfreeze", userId, assetId, amount);
        }
    }

    /**
     * 尝试执行资产转移操作。
     *
     * @param type     转移类型 (AVAILABLE_TO_AVAILABLE, AVAILABLE_TO_FROZEN, FROZEN_TO_AVAILABLE)
     * @param fromUser 转移资产的源用户 ID
     * @param toUser   转移资产的目标用户 ID
     * @param assetId  资产类型的枚举值
     * @param amount   转移金额
     * @throws RuntimeException 如果转移失败，抛出运行时异常
     */
    public void transfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount) {
        if (!tryTransfer(type, fromUser, toUser, assetId, amount, true)) {
            throw new AssetException("Transfer failed", fromUser, assetId, amount);
        }
    }

    /**
     * 核心资产转移逻辑。
     *
     * @param type         转移类型
     * @param fromUser     转移的源用户 ID
     * @param toUser       转移的目标用户 ID
     * @param assetId      资产类型
     * @param amount       转移金额
     * @param checkBalance 是否检查余额
     * @return 如果转移成功，返回 true；否则返回 false
     */
    public boolean tryTransfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount, boolean checkBalance) {
        validateAmount(amount, fromUser, assetId);

        // 按用户 ID 排序，确保锁顺序一致
        Long firstUser = Math.min(fromUser, toUser);
        Long secondUser = Math.max(fromUser, toUser);

        // 锁定用户资源，确保操作的线程安全
        try {
            // 尝试获取两个用户的锁
            boolean locked = userLockManager.tryLockBoth(firstUser, secondUser, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new AssetException("Failed to acquire necessary locks", fromUser, assetId, amount);
            }

            // 初始化资产
            Asset fromAsset = initAssets(fromUser, assetId);
            Asset toAsset = initAssets(toUser, assetId);

            // 执行转移逻辑
            return executeTransfer(type, fromAsset, toAsset, amount, checkBalance);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during lock acquisition", e);
            throw new AssetException("Lock acquisition interrupted", null, assetId, amount);
        } finally {
            // 释放锁
            userLockManager.unlockBoth(firstUser, secondUser);
        }
    }

    /**
     * 初始化用户资产，如果尚未存在。
     *
     * @param userId  用户 ID
     * @param assetId 资产类型
     */
    public void initializeAsset(Long userId, AssetEnum assetId) {
        // 初始化资产对象，默认可用余额和冻结余额为 0
        initAssets(userId, assetId);
    }

    //=====================================私有方法=================================

    /**
     * 执行实际的转移逻辑。
     */
    private boolean executeTransfer(Transfer type, Asset fromAsset, Asset toAsset, BigDecimal amount, boolean checkBalance) {
        return switch (type) {
            case AVAILABLE_TO_AVAILABLE -> transferBalance(fromAsset, toAsset, amount, checkBalance, true, true);
            case AVAILABLE_TO_FROZEN -> transferBalance(fromAsset, toAsset, amount, checkBalance, true, false);
            case FROZEN_TO_AVAILABLE -> transferBalance(fromAsset, toAsset, amount, checkBalance, false, true);
            default -> throw new AssetException("Invalid transfer type", null, null, amount);
        };
    }

    /**
     * 转移资产的核心逻辑。
     *
     * @param fromAsset     源资产对象
     * @param toAsset       目标资产对象
     * @param amount        转移金额
     * @param checkBalance  是否检查余额
     * @param fromAvailable 是否从源资产的可用余额中扣减（true 表示从可用余额扣减，false 表示从冻结余额扣减）
     * @param toAvailable   是否将金额添加到目标资产的可用余额中（true 表示添加到可用余额，false 表示添加到冻结余额）
     * @return 如果转移成功，返回 true；如果余额不足且启用了余额检查，返回 false
     */
    private boolean transferBalance(Asset fromAsset, Asset toAsset, BigDecimal amount,
                                    boolean checkBalance, boolean fromAvailable,
                                    boolean toAvailable) {
        BigDecimal sourceBalance = fromAvailable ? fromAsset.available : fromAsset.frozen;

        // 检查余额
        if (checkBalance && sourceBalance.compareTo(amount) < 0) {
            // 余额不足
            return false;
        }

        // 执行转移
        if (fromAvailable) {
            fromAsset.available = fromAsset.available.subtract(amount);
        } else {
            fromAsset.frozen = fromAsset.frozen.subtract(amount);
        }

        if (toAvailable) {
            toAsset.available = toAsset.available.add(amount);
        } else {
            toAsset.frozen = toAsset.frozen.add(amount);
        }
        // 转移成功
        return true;
    }

    /**
     * 获取用户资产映射，如果不存在，则初始化
     *
     * @param userId  用户 ID
     * @param assetId 资产类型的枚举值
     * @return 新初始化的资产对象，如果资产已存在，则返回已有资产。
     */
    private Asset initAssets(Long userId, AssetEnum assetId) {
        return userLockManager.executeWithLock(
                userLockManager.getLock(userId),
                LockType.WRITE,
                () -> userAssets
                        .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(assetId, k -> new Asset())
        );
    }

    /**
     * 验证金额合法性。
     */
    private void validateAmount(BigDecimal amount, Long userId, AssetEnum assetId) {
        if (amount.signum() <= 0) {
            throw new AssetException("Amount must be positive", userId, assetId, amount);
        }
    }
}
