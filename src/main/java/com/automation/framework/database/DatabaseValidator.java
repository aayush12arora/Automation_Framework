package com.automation.framework.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Generic database assertions/validations built on {@link DatabaseQueryExecutor}.
 * No domain-specific queries - callers pass their own SQL.
 */
public class DatabaseValidator {

    private static final Logger log = LogManager.getLogger(DatabaseValidator.class);
    private final DatabaseQueryExecutor executor = new DatabaseQueryExecutor();

    /** True if the query returns at least one row. */
    public boolean rowExists(String sql, Object... params) {
        return !executor.executeQuery(sql, params).isEmpty();
    }

    /** First value of the first column of the first row, or null. */
    public Object singleValue(String sql, Object... params) {
        List<Map<String, Object>> rows = executor.executeQuery(sql, params);
        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0).values().iterator().next();
    }

    /**
     * Poll a query until it returns at least one row or the timeout elapses.
     * Useful for waiting on asynchronous back-end writes.
     */
    public boolean waitForRow(String sql, int timeoutSeconds, Object... params) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (rowExists(sql, params)) {
                return true;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.warn("Timed out after {}s waiting for row from: {}", timeoutSeconds, sql);
        return false;
    }
}
