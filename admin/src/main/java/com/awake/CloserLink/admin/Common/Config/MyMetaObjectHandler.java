package com.awake.CloserLink.admin.Common.Config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author 清醒
 * @Date 2024/5/14 22:54
 * <p>
 * 字段自动填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
/***
 * 新增数据填充(mybatis-plus)
 */


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        strictInsertFill(metaObject, "createTime", Date::new, Date.class); // 起始版本 3.3.3(推荐)

        strictInsertFill(metaObject, "updateTime", Date::new, Date.class); // 起始版本 3.3.3(推荐)

        strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class); // 起始版本 3.3.3(推荐)

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        strictUpdateFill(metaObject, "updateTime", Date::new,Date.class); // 起始版本 3.3.0(推荐)

    }

}