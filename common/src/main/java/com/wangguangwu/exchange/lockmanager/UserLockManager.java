package com.wangguangwu.tradingengine.assets.lockmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wangguangwu
 */
@Component
@Slf4j
public class UserLockManager {

    /**
     * 用户锁映射：用户ID -> 用户的读写锁
     */
    private final ConcurrentMap<Long, ReentrantReadWriteLock> userLocks = new ConcurrentHashMap<>();

    /**
     * 清理不再使用的锁的阈值（毫秒）
     */
    private static final long LOCK_EXPIRATION_THRESHOLD = 10 * 60 * 1000;

    /**
     * 锁的使用时间戳映射
     */
    private final ConcurrentMap<Long, Long> lockUsageTimestamps = new ConcurrentHashMap<>();

    /**
     * 获取用户的读写锁。
     *
     * @param userId 用户 ID
     * @return 用户的读写锁
     */
    public ReentrantReadWriteLock getLock(Long userId) {
        lockUsageTimestamps.put(userId, System.currentTimeMillis());
        return userLocks.computeIfAbsent(userId, k -> new ReentrantReadWriteLock());
    }

    /**
     * 尝试获取两个用户的锁，按顺序获取，避免死锁。
     *
     * @param firstUserId  第一个用户 ID
     * @param secondUserId 第二个用户 ID
     * @param timeout      超时时间
     * @param unit         时间单位
     * @return 如果成功获取两个锁，返回 true；否则返回 false
     * @throws InterruptedException 如果线程被中断
     */
    public boolean tryLockBoth(Long firstUserId, Long secondUserId, long timeout, TimeUnit unit) throws InterruptedException {
        // 按用户ID排序，确保锁顺序一致，避免死锁
        Long lowerId = Math.min(firstUserId, secondUserId);
        Long higherId = Math.max(firstUserId, secondUserId);

        ReentrantReadWriteLock lowerLock = getLock(lowerId);
        ReentrantReadWriteLock higherLock = getLock(higherId);

        if (!lowerLock.writeLock().tryLock(timeout, unit)) {
            return false;
        }
        if (!higherLock.writeLock().tryLock(timeout, unit)) {
            lowerLock.writeLock().unlock();
            return false;
        }
        return true;
    }

    /**
     * 释放两个用户的锁。
     *
     * @param firstUserId  第一个用户 ID
     * @param secondUserId 第二个用户 ID
     */
    public void unlockBoth(Long firstUserId, Long secondUserId) {
        // 按用户ID排序，确保锁释放顺序与获取一致
        Long lowerId = Math.min(firstUserId, secondUserId);
        Long higherId = Math.max(firstUserId, secondUserId);

        unlock(getLock(higherId));
        unlock(getLock(lowerId));
    }

    /**
     * 释放单个锁。
     *
     * @param lock 要释放的锁
     */
    private void unlock(ReentrantReadWriteLock lock) {
        if (lock.writeLock().isHeldByCurrentThread()) {
            try {
                lock.writeLock().unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("Lock was not held, ignoring unlock", e);
            }
        }
    }

    /**
     * 定期清理不再使用的锁，避免内存泄漏。
     * <p>
     * 每10分钟执行一次
     */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanExpiredLocks() {
        long now = System.currentTimeMillis();

        lockUsageTimestamps.forEach((userId, lastUsed) -> {
            if (now - lastUsed > LOCK_EXPIRATION_THRESHOLD) {
                userLocks.remove(userId);
                lockUsageTimestamps.remove(userId);
                log.info("Removed expired lock for user {}", userId);
            }
        });
    }
}
