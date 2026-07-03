package com.automation.framework.database;

import com.automation.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes parameterized SQL and maps results to plain {@code List<Map>} rows.
 * Generic and vendor-neutral - works with any JDBC-compliant database.
 */
public class DatabaseQueryExecutor {

    private static final Logger log = LogManager.getLogger(DatabaseQueryExecutor.class);

    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        log.info("SQL query: {}", sql);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return mapRows(rs);
            }
        } catch (Exception e) {
            throw new FrameworkException("Query failed: " + sql, e);
        }
    }

    public int executeUpdate(String sql, Object... params) {
        log.info("SQL update: {}", sql);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new FrameworkException("Update failed: " + sql, e);
        }
    }

    private static void bind(PreparedStatement ps, Object... params) throws Exception {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }

    private static List<Map<String, Object>> mapRows(ResultSet rs) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int c = 1; c <= columns; c++) {
                row.put(meta.getColumnLabel(c), rs.getObject(c));
            }
            rows.add(row);
        }
        return rows;
    }
}
