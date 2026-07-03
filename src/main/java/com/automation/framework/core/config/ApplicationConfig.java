package com.automation.framework.core.config;

import com.automation.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The complete, self-contained description of one application under test.
 * Everything the framework needs to drive an application lives here and is
 * loaded from {@code resources/applications/<name>/application.yml}.
 *
 * <p>Onboarding a brand new application is therefore purely additive: drop a
 * new folder with an {@code application.yml}, no Java changes required.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationConfig {

    /** Application name (should match the folder name). */
    private String name;

    /** Environment name -> environment definition (dev/stage/prod/...). */
    private Map<String, EnvironmentConfig> environments = new LinkedHashMap<>();

    /** Page name -> page definition. */
    private Map<String, PageConfig> pages = new LinkedHashMap<>();

    /** Optional database configuration for validation tests. */
    private DatabaseConfig database;

    /** Free-form test data available to tests and flows. */
    private Map<String, Object> testData = new LinkedHashMap<>();

    /**
     * Resolve an environment by name, failing clearly when it is missing.
     */
    public EnvironmentConfig environment(String environmentName) {
        EnvironmentConfig env = environments.get(environmentName);
        if (env == null) {
            throw new FrameworkException("Environment '" + environmentName
                    + "' is not defined for application '" + name
                    + "'. Known environments: " + environments.keySet());
        }
        return env;
    }

    /**
     * Resolve a page by name. Page objects and flows reference pages by this key.
     */
    public PageConfig page(String pageName) {
        PageConfig page = pages.get(pageName);
        if (page == null) {
            throw new FrameworkException("Page '" + pageName
                    + "' is not defined for application '" + name
                    + "'. Known pages: " + pages.keySet());
        }
        // Ensure the page carries its own name even if omitted in config.
        if (page.getName() == null || page.getName().isBlank()) {
            page.setName(pageName);
        }
        return page;
    }
}
