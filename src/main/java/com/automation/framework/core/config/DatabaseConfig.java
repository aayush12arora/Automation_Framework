package com.automation.framework.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional database connection details for data validation tests.
 * Driver-agnostic: supply any JDBC URL and add the matching driver dependency
 * to the project. Absent for applications that do not need DB validation.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseConfig {

    /** Full JDBC URL, e.g. "jdbc:postgresql://host:5432/mydb". */
    private String jdbcUrl;

    private String username;
    private String password;

    /** Optional explicit driver class; usually auto-detected from the URL. */
    private String driverClassName;

    private int poolSize = 5;
}
