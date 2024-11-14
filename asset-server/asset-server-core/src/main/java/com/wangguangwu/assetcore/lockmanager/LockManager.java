package com.wangguangwu.assetcore.lockmanager;

import com.wangguangwu.assetcore.enums.LockType;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通用锁管理器，提供读写锁的统一处理逻辑。
 *
 * @author wangguangwu
 */
public class LockManager {

    /**
     * 通用的锁操作模板方法。
     *
     * @param lock     锁对象
     * @param lockType 锁类型（读锁或写锁）
     * @param task     需要执行的逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithLock(ReentrantReadWriteLock lock, LockType lockType, Callable<T> task) {
        Lock actualLock = lockType == LockType.READ ? lock.readLock() : lock.writeLock();
        actualLock.lock();
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException("Error during lock-protected operation", e);
        } finally {
            actualLock.unlock();
        }
    }

    /**
     * 通用的锁操作模板方法。
     *
     * @param lock     锁对象
     * @param lockType 锁类型（读锁或写锁）
     * @param task     需要执行的逻辑
     */
    public void executeWithLock(ReentrantReadWriteLock lock, LockType lockType, Runnable task) {
        Lock actualLock = lockType == LockType.READ ? lock.readLock() : lock.writeLock();
        actualLock.lock();
        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException("Error during lock-protected operation", e);
        } finally {
            actualLock.unlock();
        }
    }
}
