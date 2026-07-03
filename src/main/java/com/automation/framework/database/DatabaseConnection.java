package com.automation.framework.database;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.config.DatabaseConfig;
import com.automation.framework.exceptions.FrameworkException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection-pooled database access via HikariCP. Driver-agnostic: the JDBC URL
 * and (optionally) driver class come from the application config, and the
 * matching JDBC driver is added as a project dependency per application.
 */
public final class DatabaseConnection {

    private static final Logger log = LogManager.getLogger(DatabaseConnection.class);
    private static volatile HikariDataSource dataSource;

    private DatabaseConnection() {
    }

    private static HikariDataSource dataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConnection.class) {
                if (dataSource == null) {
                    dataSource = build();
                }
            }
        }
        return dataSource;
    }

    private static HikariDataSource build() {
        DatabaseConfig db = ConfigurationManager.getInstance().getApplicationConfig().getDatabase();
        if (db == null || db.getJdbcUrl() == null || db.getJdbcUrl().isBlank()) {
            throw new FrameworkException("No database configured for application '"
                    + ConfigurationManager.getInstance().getApplicationName()
                    + "'. Add a 'database:' block to application.yml to use DB validation.");
        }
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(db.getJdbcUrl());
        cfg.setUsername(db.getUsername());
        cfg.setPassword(db.getPassword());
        if (db.getDriverClassName() != null && !db.getDriverClassName().isBlank()) {
            cfg.setDriverClassName(db.getDriverClassName());
        }
        cfg.setMaximumPoolSize(db.getPoolSize());
        cfg.setPoolName("automation-pool");
        log.info("Initialising DB pool for {}", db.getJdbcUrl());
        return new HikariDataSource(cfg);
    }

    public static Connection getConnection() {
        try {
            return dataSource().getConnection();
        } catch (SQLException e) {
            throw new FrameworkException("Failed to obtain a database connection", e);
        }
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
