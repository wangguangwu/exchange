package com.wangguangwu.exchange.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * MyBatis-Plus 配置类，负责全局字段的自动填充和插件配置。
 * 提供了自动填充 `createTime` 和 `updateTime` 字段的功能，使用分页和乐观锁插件。
 *
 * @author wangguangwu
 */
@Configuration
public class MyBatisPlusConfiguration {

    /**
     * 配置全局通用字段自动填充处理器。
     * 用于在插入和更新操作时自动填充 `createTime` 和 `updateTime` 字段。
     *
     * @return MetaObjectHandler 用于处理字段的自动填充
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 自定义 MetaObjectHandler 实现类，用于处理实体类中字段的自动填充逻辑。
     */
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // 自动填充 createTime 和 updateTime 字段
            fillIfNull(metaObject, "createTime", LocalDateTime::now);
            fillIfNull(metaObject, "updateTime", LocalDateTime::now);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 仅自动填充 updateTime 字段
            fillIfNull(metaObject, "updateTime", LocalDateTime::now);
        }

        /**
         * 如果指定字段为空，则进行填充。
         *
         * @param metaObject       MyBatis 提供的元对象
         * @param fieldName        字段名称
         * @param fieldValSupplier 填充的值提供者
         */
        private void fillIfNull(MetaObject metaObject, String fieldName, Supplier<Object> fieldValSupplier) {
            if (metaObject.hasGetter(fieldName) && getFieldValByName(fieldName, metaObject) == null) {
                this.strictInsertFill(metaObject, fieldName, fieldValSupplier, Object.class);
            }
        }
    }

    /**
     * 配置 MyBatis-Plus 插件。
     * 包含分页插件和乐观锁插件，用于优化分页查询和并发控制。
     *
     * @return MybatisPlusInterceptor MyBatis-Plus 插件拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件，支持 MySQL 数据库
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件，确保数据的一致性和并发控制
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
