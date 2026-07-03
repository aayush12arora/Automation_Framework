package com.automation.framework.core.driver;

import com.automation.framework.exceptions.FrameworkException;

/**
 * Supported browsers. New browsers are added here and handled in
 * {@link com.automation.framework.core.driver.DriverFactory}.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    /**
     * Case-insensitive lookup used when resolving the {@code browser} property.
     */
    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        try {
            return BrowserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("Unsupported browser: '" + value
                    + "'. Supported: CHROME, FIREFOX, EDGE, SAFARI");
        }
    }
}
