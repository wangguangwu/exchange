package com.wangguangwu.assetcore.assets;

import com.wangguangwu.assetcore.enums.AssetEnum;
import com.wangguangwu.assetcore.enums.Transfer;
import com.wangguangwu.assetcore.exception.AssetException;
import com.wangguangwu.assetcore.lockmanager.UserLockManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangguangwu
 */
class AssetServiceTest {

    private AssetService assetService;

    @BeforeEach
    void setUp() {
        // 初始化测试对象
        assetService = new AssetService(new UserLockManager());
    }

    @Test
    void getAsset() {
        Long userId = 1L;
        AssetEnum assetId = AssetEnum.BTC;

        // 验证未初始化的资产返回 null
        Assertions.assertNull(assetService.getAsset(userId, assetId), "Asset should be null before initialization");

        // 初始化资产并验证
        assetService.initializeAsset(userId, assetId);
        Asset asset = assetService.getAsset(userId, assetId);
        Assertions.assertNotNull(asset, "Asset should be initialized");
        Assertions.assertEquals(BigDecimal.ZERO, asset.getAvailable(), "Available balance should be 0");
        Assertions.assertEquals(BigDecimal.ZERO, asset.getFrozen(), "Frozen balance should be 0");
    }

    @Test
    void addAndValidateAsset() {
        Long userId = 1L;
        AssetEnum assetId = AssetEnum.BTC;

        // 初始化资产
        assetService.initializeAsset(userId, assetId);

        // 增加资产并验证
        assetService.addAsset(userId, assetId, BigDecimal.valueOf(50));
        Asset asset = assetService.getAsset(userId, assetId);
        Assertions.assertEquals(BigDecimal.valueOf(50), asset.getAvailable(), "Available balance should be updated correctly");
        Assertions.assertEquals(BigDecimal.ZERO, asset.getFrozen(), "Frozen balance should remain 0");

        // 增加负数资产应抛出异常
        Assertions.assertThrows(AssetException.class, () ->
                assetService.addAsset(userId, assetId, BigDecimal.valueOf(-10)), "Adding negative amount should throw exception");
    }

    @Test
    void concurrentAssetModification() throws InterruptedException {
        Long userId = 1L;
        AssetEnum assetId = AssetEnum.BTC;

        // 初始化资产
        assetService.initializeAsset(userId, assetId);

        // 并发增加资产
        int threads = 10;
        runConcurrentTest(threads, () -> assetService.addAsset(userId, assetId, BigDecimal.valueOf(10)));

        // 验证结果
        Asset asset = assetService.getAsset(userId, assetId);
        Assertions.assertEquals(BigDecimal.valueOf(100), asset.getAvailable(), "Available balance should match expected value");
    }

    @Test
    void concurrentTransferTest() throws InterruptedException {
        Long fromUser = 1L;
        Long toUser = 2L;
        AssetEnum assetId = AssetEnum.USD;

        // 初始化用户资产
        assetService.initializeAsset(fromUser, assetId);
        assetService.initializeAsset(toUser, assetId);
        assetService.addAsset(fromUser, assetId, BigDecimal.valueOf(100));

        // 并发转账
        int threads = 10;
        AtomicInteger successfulTransfers = new AtomicInteger();
        runConcurrentTest(threads, () -> {
            boolean success = assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, fromUser, toUser, assetId, BigDecimal.valueOf(10), true);
            if (success) successfulTransfers.incrementAndGet();
        });

        // 验证最终结果
        Asset fromAsset = assetService.getAsset(fromUser, assetId);
        Asset toAsset = assetService.getAsset(toUser, assetId);
        Assertions.assertEquals(
                BigDecimal.valueOf(100 - successfulTransfers.get() * 10L),
                fromAsset.getAvailable(),
                "From user's balance should decrease correctly"
        );
        Assertions.assertEquals(
                BigDecimal.valueOf(successfulTransfers.get() * 10L),
                toAsset.getAvailable(),
                "To user's balance should increase correctly"
        );
    }

    @Test
    void concurrentFreezeAndUnfreezeTest() throws InterruptedException {
        Long userId = 1L;
        AssetEnum assetId = AssetEnum.BTC;

        // 初始化资产并增加余额
        assetService.initializeAsset(userId, assetId);
        assetService.addAsset(userId, assetId, BigDecimal.valueOf(100));

        // 并发冻结资产
        int threads = 5;
        runConcurrentTest(threads, () -> assetService.tryFreeze(userId, assetId, BigDecimal.valueOf(10)));

        // 验证冻结结果
        Asset asset = assetService.getAsset(userId, assetId);
        Assertions.assertEquals(BigDecimal.valueOf(50), asset.getAvailable(), "Available balance should decrease after freezing");
        Assertions.assertEquals(BigDecimal.valueOf(50), asset.getFrozen(), "Frozen balance should increase after freezing");

        // 并发解冻资产
        runConcurrentTest(threads, () -> assetService.unfreeze(userId, assetId, BigDecimal.valueOf(10)));

        // 验证解冻结果
        Assertions.assertEquals(BigDecimal.valueOf(100), asset.getAvailable(), "Available balance should increase after unfreezing");
        Assertions.assertEquals(BigDecimal.ZERO, asset.getFrozen(), "Frozen balance should be zero after unfreezing");
    }

    @Test
    void transferFailureShouldMaintainState() {
        Long fromUser = 1L;
        Long toUser = 2L;
        AssetEnum assetId = AssetEnum.BTC;

        // 初始化用户资产并设置余额
        assetService.initializeAsset(fromUser, assetId);
        assetService.initializeAsset(toUser, assetId);
        assetService.addAsset(fromUser, assetId, BigDecimal.valueOf(50));

        // 转账超出余额应失败
        boolean success = assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, fromUser, toUser, assetId, BigDecimal.valueOf(100), true);
        Assertions.assertFalse(success, "Transfer exceeding balance should fail");

        // 验证状态未变
        Asset fromAsset = assetService.getAsset(fromUser, assetId);
        Asset toAsset = assetService.getAsset(toUser, assetId);
        Assertions.assertEquals(BigDecimal.valueOf(50), fromAsset.getAvailable(), "From user's balance should remain unchanged");
        Assertions.assertEquals(BigDecimal.ZERO, toAsset.getAvailable(), "To user's balance should remain unchanged");
    }

    //=====================================辅助方法=================================

    /**
     * 并发测试辅助方法。
     *
     * @param threads 线程数量
     * @param task    并发执行的任务
     * @throws InterruptedException 如果线程被中断
     */
    private void runConcurrentTest(int threads, Runnable task) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Assertions.fail("Thread interrupted during test execution");
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 所有线程准备好后同时开始
        startLatch.countDown();
        // 等待所有线程完成
        endLatch.await();
        executorService.shutdown();
    }
}