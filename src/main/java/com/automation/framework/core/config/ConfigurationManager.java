package com.automation.framework.core.config;

import com.automation.framework.core.driver.BrowserType;
import com.automation.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Thread-safe singleton that is the single source of truth for all configuration.
 *
 * <p>Resolution order for any global setting (highest priority first):
 * <ol>
 *   <li>JVM system property / {@code -D} flag (e.g. {@code -Dbrowser=firefox})</li>
 *   <li>OS environment variable (upper snake case, e.g. {@code BROWSER})</li>
 *   <li>{@code config/framework.properties} on the classpath</li>
 * </ol>
 *
 * <p>The active application's structural config (environments, pages, elements)
 * is loaded from {@code applications/<application>/application.yml}.
 */
public final class ConfigurationManager {

    private static final Logger log = LogManager.getLogger(ConfigurationManager.class);
    private static final String FRAMEWORK_PROPERTIES = "config/framework.properties";

    private static volatile ConfigurationManager instance;

    private final Properties properties = new Properties();
    private final ApplicationConfig applicationConfig;
    private final String environmentName;

    private ConfigurationManager() {
        loadFrameworkProperties();
        this.environmentName = get("environment", "prod");
        this.applicationConfig = loadApplicationConfig(get("application", "gyansathi"));
        log.info("Configuration initialised: application='{}', environment='{}', browser='{}', headless={}",
                applicationConfig.getName(), environmentName, getBrowser(), isHeadless());
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    /** Visible for tests: forces the configuration to be rebuilt on next access. */
    public static synchronized void reset() {
        instance = null;
    }

    // ------------------------------------------------------------------ loading

    private void loadFrameworkProperties() {
        try (InputStream in = classpath(FRAMEWORK_PROPERTIES)) {
            if (in == null) {
                throw new FrameworkException("Could not find " + FRAMEWORK_PROPERTIES + " on the classpath");
            }
            properties.load(in);
        } catch (Exception e) {
            throw new FrameworkException("Failed to load framework properties", e);
        }
    }

    private ApplicationConfig loadApplicationConfig(String application) {
        String resource = "applications/" + application + "/application.yml";
        try (InputStream in = classpath(resource)) {
            if (in == null) {
                throw new FrameworkException("No application config found at '" + resource
                        + "'. Create it to onboard application '" + application + "'.");
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            ApplicationConfig config = mapper.readValue(in, ApplicationConfig.class);
            if (config.getName() == null || config.getName().isBlank()) {
                config.setName(application);
            }
            // Backfill page names from their map keys so lookups always resolve.
            config.getPages().forEach((key, page) -> {
                if (page.getName() == null || page.getName().isBlank()) {
                    page.setName(key);
                }
            });
            return config;
        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException("Failed to parse application config '" + resource + "'", e);
        }
    }

    private static InputStream classpath(String resource) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }

    // --------------------------------------------------------------- raw getters

    /**
     * Resolve a raw setting using the documented precedence. Blank values and
     * unresolved Maven placeholders (e.g. literal "${browser}") are ignored so
     * that they never mask a real value from the properties file.
     */
    public String get(String key, String defaultValue) {
        String sys = sanitize(System.getProperty(key));
        if (sys != null) {
            return sys;
        }
        String env = sanitize(System.getenv(key.toUpperCase().replace('.', '_')));
        if (env != null) {
            return env;
        }
        String prop = sanitize(properties.getProperty(key));
        return prop != null ? prop : defaultValue;
    }

    public String get(String key) {
        return get(key, null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("${")) {
            return null;
        }
        return trimmed;
    }

    // ----------------------------------------------------------- typed accessors

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public String getApplicationName() {
        return applicationConfig.getName();
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public EnvironmentConfig getEnvironment() {
        return applicationConfig.environment(environmentName);
    }

    /** Web base URL for the active environment. */
    public String getBaseUrl() {
        String baseUrl = getEnvironment().getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new FrameworkException("baseUrl is not set for environment '" + environmentName
                    + "' of application '" + getApplicationName() + "'");
        }
        return baseUrl;
    }

    /** API base URL for the active environment (may be null). */
    public String getApiBaseUrl() {
        return getEnvironment().getApiBaseUrl();
    }

    public BrowserType getBrowser() {
        return BrowserType.from(get("browser", "chrome"));
    }

    public boolean isHeadless() {
        return getBoolean("headless", true);
    }

    public boolean isIncognito() {
        return getBoolean("browser.incognito", false);
    }

    /** Explicit browser binary path, or null to auto-detect. */
    public String getBrowserBinary() {
        return get("browser.binary");
    }

    /** Explicit driver binary path, or null to let Selenium Manager provision it. */
    public String getDriverPath() {
        return get("driver.path");
    }

    public String getWindowSize() {
        return get("window.size", "1920x1080");
    }

    public Duration getExplicitTimeout() {
        return Duration.ofSeconds(getInt("timeout.explicit", 20));
    }

    public Duration getImplicitTimeout() {
        return Duration.ofSeconds(getInt("timeout.implicit", 0));
    }

    public Duration getPageLoadTimeout() {
        return Duration.ofSeconds(getInt("timeout.pageload", 45));
    }

    public Duration getPollingInterval() {
        return Duration.ofMillis(getInt("timeout.polling.millis", 250));
    }

    public boolean isRemoteExecution() {
        return getBoolean("remote.execution", false);
    }

    public String getRemoteUrl() {
        return get("remote.url");
    }

    public String getRemoteProvider() {
        return get("remote.provider", "local");
    }

    public int getRetryCount() {
        return getInt("retry.count", 1);
    }

    public boolean screenshotOnFailure() {
        return getBoolean("screenshot.on.failure", true);
    }

    public boolean screenshotOnPass() {
        return getBoolean("screenshot.on.pass", false);
    }

    public String getReportPath() {
        return get("report.path", "test-output/reports");
    }

    public String getReportName() {
        return get("report.name", "Automation Report");
    }

    public String getScreenshotPath() {
        return get("screenshot.path", "test-output/screenshots");
    }

    public String getFlowsRoot() {
        return get("flows.root", "flows");
    }

    public boolean isFlowsFailFast() {
        return getBoolean("flows.failfast", true);
    }
}
