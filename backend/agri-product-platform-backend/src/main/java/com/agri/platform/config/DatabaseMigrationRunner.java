package com.agri.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 启动时自动执行的数据库迁移（幂等）。
 * <p>
 * 每次后端启动（含在 IDEA 中运行）都会执行一次：先用 information_schema 检查目标列是否已存在，
 * 不存在才执行 ALTER TABLE ADD COLUMN，存在则跳过 —— 因此可重复执行、不会破坏现有数据。
 * <p>
 * 目的：团队成员只需在 IDEA 里启动后端，数据库就会自动升级到最新结构，无需手动跑 SQL 脚本。
 * 对应的人工脚本仍保留在 backend/database/ 下，供命令行手动执行（结果与本 Runner 一致）。
 */
@Slf4j
@Component
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("[DB迁移] 开始执行幂等数据库迁移（若目标列已存在则自动跳过）...");

        // 2026-07-14：角色申请表增加「相关材料」字段（对应 database/migration_20260714.sql）
        addColumnIfNotExists("tb_role_application", "materials",
                "VARCHAR(500) DEFAULT NULL COMMENT '相关材料文件URL（资质/证明等，可选）'");

        // 2026-07-17：融资申请表增加「申请原因 / 还款来源」（对应 database/migration_20260717.sql）
        addColumnIfNotExists("tb_finance", "purpose",
                "VARCHAR(500) DEFAULT NULL COMMENT '申请原因/融资用途'");
        addColumnIfNotExists("tb_finance", "repayment_source",
                "VARCHAR(500) DEFAULT NULL COMMENT '还款来源'");

        log.info("[DB迁移] 完成。");
    }

    /**
     * 幂等加列：列已存在则跳过，不存在则 ADD COLUMN。
     * 使用 DATABASE() 取当前连接的库（即 application.yml 中 url 指向的 agri_platform），避免硬编码库名。
     */
    private void addColumnIfNotExists(String table, String column, String columnDefinition) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS "
                            + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                    Integer.class, table, column);

            if (count != null && count > 0) {
                log.info("[DB迁移] {}.{} 列已存在，跳过", table, column);
                return;
            }

            String sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + columnDefinition;
            jdbcTemplate.execute(sql);
            log.info("[DB迁移] 已新增列 {}.{}", table, column);
        } catch (Exception e) {
            // 表尚未建好（新环境未导入建表脚本）等情况，打印警告但不阻断启动
            log.warn("[DB迁移] 新增列 {}.{} 失败，已跳过（不阻断启动）：{}", table, column, e.getMessage());
        }
    }
}
