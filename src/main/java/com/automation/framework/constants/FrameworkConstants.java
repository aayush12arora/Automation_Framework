package com.automation.framework.constants;

/**
 * Central place for framework-wide constants that are not user-configurable.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final String FRAMEWORK_PROPERTIES = "config/framework.properties";
    public static final String APPLICATIONS_ROOT = "applications";
    public static final String APPLICATION_CONFIG_FILE = "application.yml";

    /** TestNG groups used across the suite. */
    public static final String GROUP_SMOKE = "smoke";
    public static final String GROUP_REGRESSION = "regression";
    public static final String GROUP_API = "api";
}
