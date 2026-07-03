package com.automation.framework.enums;

/**
 * Standard test categories, mirrored by the TestNG group names used across the
 * suite (see {@link com.automation.framework.constants.FrameworkConstants}).
 */
public enum TestCategory {
    SMOKE,
    SANITY,
    REGRESSION,
    E2E,
    API,
    ACCESSIBILITY;

    public String groupName() {
        return name().toLowerCase();
    }
}
