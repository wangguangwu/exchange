package com.wangguangwu.exchange.utils;

import com.wangguangwu.exchange.exception.CommonServiceException;
import org.springframework.beans.BeanUtils;

/**
 * 通用对象映射工具类
 * 提供基于反射的对象属性复制功能，用于实现 DTO 与实体类之间的转换。
 *
 * <p>适用于需要将一个对象的属性快速映射到另一个对象的场景。</p>
 *
 * @author wangguangwu
 */
public final class MappingUtils {

    private MappingUtils() {
        // 工具类无需实例化
    }

    /**
     * 通用对象映射方法
     *
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象实例
     */
    public static <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new CommonServiceException("对象映射失败", e);
        }
    }
}
