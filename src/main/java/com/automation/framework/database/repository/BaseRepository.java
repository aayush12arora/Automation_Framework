package com.automation.framework.database.repository;

import com.automation.framework.database.DatabaseQueryExecutor;

import java.util.List;
import java.util.Map;

/**
 * Generic base for the repository pattern. Application-specific repositories
 * extend this and expose their own query methods; the base provides the shared
 * executor and common helpers so no domain logic lives here.
 */
public abstract class BaseRepository {

    protected final DatabaseQueryExecutor executor = new DatabaseQueryExecutor();

    protected List<Map<String, Object>> query(String sql, Object... params) {
        return executor.executeQuery(sql, params);
    }

    protected int update(String sql, Object... params) {
        return executor.executeUpdate(sql, params);
    }
}
